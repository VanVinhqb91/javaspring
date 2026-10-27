package vpasound.com.backend.reports.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
@RequestMapping("/backend/reports")
public class SaleOrdersController {
	
	@GetMapping("/sale_order")
	public String listReportSaleOrders() {
		return "/backend/reports/sale_orders/list";
	}
}
