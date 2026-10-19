package vpasound.com.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import vpasound.com.core.entity.OrderEntity;

public interface OrderRepository extends JpaRepository<OrderEntity, Long>{

}
