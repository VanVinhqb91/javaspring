package vpasound.com.backend.sales.controller;

import vpasound.com.backend.sales.service.AddressService;
import vpasound.com.core.dto.response.backend.AddressResponseDto;
import vpasound.com.core.entity.OrderEntity;
import vpasound.com.core.helper.AddressSnapshot;
import vpasound.com.core.repository.OrderRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/address")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AddressController {
	private final AddressService addressService;
	private final OrderRepository orderRepository;

    // URL test: GET http://vpasound.com/api/v1/address/provinces
    @GetMapping(value = "/provinces", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AddressResponseDto>> getProvinces() {
        return ResponseEntity.ok(addressService.getAllProvinces());
    }

    // URL test: GET http://vpasound.com/api/v1/address/districts?provinceId=1
    @GetMapping(value = "/districts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AddressResponseDto>> getDistricts(@RequestParam("province_id") Long provinceId) {
        return ResponseEntity.ok(addressService.getDistrictsByProvince(provinceId));
    }

    // URL test: GET http://vpasound.com/api/v1/address/wards?districtId=5
    @GetMapping(value = "/wards", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AddressResponseDto>> getWards(@RequestParam("district_id") Long districtId) {
        return ResponseEntity.ok(addressService.getWardsByDistrict(districtId));
    }
    
    @PutMapping("/order/{orderId}/update-address")
    public ResponseEntity<?> updateOrderAddress(
        @PathVariable Long orderId,
        @RequestParam("type") String type,
        @RequestBody AddressSnapshot addressSnapshot
    ) {
        System.out.println(type);
        // 1. Tìm đơn hàng trong DB, nếu không thấy thì báo lỗi
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng!"));

        // 2. Kiểm tra loại địa chỉ gửi lên để đặt vào đúng cột JSON trong MySQL
        if ("billing".equalsIgnoreCase(type)) {
            order.setBillingAddress(addressSnapshot);
        } else if ("shipping".equalsIgnoreCase(type)) {
            order.setShippingAddress(addressSnapshot);
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Loại địa chỉ không hợp lệ!"));
        }

        // 3. Lưu cập nhật lại vào MySQL
        OrderEntity updatedOrder = orderRepository.save(order);
        return ResponseEntity.ok(updatedOrder);
    }
}
