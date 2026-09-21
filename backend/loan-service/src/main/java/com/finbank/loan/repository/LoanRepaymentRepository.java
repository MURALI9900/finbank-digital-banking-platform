package com.finbank.loan.repository;
import com.finbank.loan.entity.LoanRepayment; import jakarta.persistence.LockModeType; import org.springframework.data.jpa.repository.JpaRepository; import org.springframework.data.jpa.repository.Lock; import org.springframework.data.jpa.repository.Query; import java.util.*;
public interface LoanRepaymentRepository extends JpaRepository<LoanRepayment,Long>{
Optional<LoanRepayment> findByRepaymentReference(String reference);
@Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select r from LoanRepayment r where r.repaymentReference = :reference") Optional<LoanRepayment> findByRepaymentReferenceForUpdate(String reference);
List<LoanRepayment> findByLoanReferenceOrderByInstallmentNumberAsc(String loanReference);
}