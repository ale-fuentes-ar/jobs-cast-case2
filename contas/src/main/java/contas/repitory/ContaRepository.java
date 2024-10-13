package contas.repitory;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import contas.model.Conta;
import jakarta.persistence.LockModeType;

public interface ContaRepository extends JpaRepository<Conta, Long> {
	
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Conta c WHERE c.id = ?1")
    Optional<Conta> findByIdWithLock(Long id);
    
}
