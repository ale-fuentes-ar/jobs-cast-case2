package contas.repitory;

import org.springframework.data.jpa.repository.JpaRepository;

import contas.model.Conta;

public interface ContaRepository extends JpaRepository<Conta, Long> {

}
