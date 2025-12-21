package com.bank;

import com.bank.core.SavingsAccount;
import com.bank.transactions.*;
import com.bank.utils.Currency;
import com.bank.utils.Money;
import com.bank.utils.TransactionStatus;
import com.bank.utils.TransactionType;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionChainTest {

    @Test
    void testTransactionRejectedDueToDailyLimit() {
        SavingsAccount account = new SavingsAccount("SAV-001");
        account.deposit(new Money(new BigDecimal("20000"), Currency.USD));

        // بناء السلسلة يدويًا سطر بسطر باستخدام setNextHandler فقط
        TransactionHandler stateValidator = new AccountStateValidator();
        TransactionHandler balanceValidator = new SufficientBalanceValidator();
        TransactionHandler limitValidator = new DailyLimitValidator();
        TransactionHandler approvalValidator = new ManagerApprovalValidator();
        TransactionHandler finalExecutor = new FinalExecutor();

        stateValidator.setNextHandler(balanceValidator);
        balanceValidator.setNextHandler(limitValidator);
        limitValidator.setNextHandler(approvalValidator);
        approvalValidator.setNextHandler(finalExecutor);

        // بدء السلسلة من أول handler
        TransactionHandler chain = stateValidator;

        Transaction transaction = new Transaction("TRX-999", account,
                new Money(new BigDecimal("15000"), Currency.USD), TransactionType.WITHDRAWAL);

        chain.handle(transaction);

        // التحقق: يجب أن تفشل بسبب الحد اليومي (DAILY_LIMIT = 10000.00)
        assertEquals(TransactionStatus.FAILED, transaction.getStatus());
    }

    // اختبار إضافي: معاملة ناجحة (أقل من الحد)
    @Test
    void testTransactionAcceptedWithinDailyLimit() {
        SavingsAccount account = new SavingsAccount("SAV-002");
        account.deposit(new Money(new BigDecimal("20000"), Currency.USD));

        TransactionHandler stateValidator = new AccountStateValidator();
        TransactionHandler balanceValidator = new SufficientBalanceValidator();
        TransactionHandler limitValidator = new DailyLimitValidator();
        TransactionHandler approvalValidator = new ManagerApprovalValidator();
        TransactionHandler finalExecutor = new FinalExecutor();

        stateValidator.setNextHandler(balanceValidator);
        balanceValidator.setNextHandler(limitValidator);
        limitValidator.setNextHandler(approvalValidator);
        approvalValidator.setNextHandler(finalExecutor);

        TransactionHandler chain = stateValidator;

        Transaction transaction = new Transaction("TRX-888", account,
                new Money(new BigDecimal("8000"), Currency.USD), TransactionType.WITHDRAWAL);

        chain.handle(transaction);

        assertEquals(TransactionStatus.COMPLETED, transaction.getStatus());
    }
}