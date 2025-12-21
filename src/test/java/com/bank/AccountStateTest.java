package com.bank;

import com.bank.core.Account;
import com.bank.core.SavingsAccount;
import com.bank.states.FrozenState;
import com.bank.utils.Currency;
import com.bank.utils.Money;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class AccountStateTest {

    @Test
    void testFrozenStateBlocksWithdrawal() {
        Account account = new SavingsAccount("SAV-001");
        account.deposit(new Money(new BigDecimal("5000"), Currency.USD));
        account.freeze(); // يغير الحالة إلى Frozen

        assertThrows(IllegalStateException.class, () ->
                account.withdraw(new Money(new BigDecimal("1000"), Currency.USD)));
    }

    @Test
    void testFrozenStateAllowsDeposit() {
        Account account = new SavingsAccount("SAV-001");
        account.freeze();
        account.deposit(new Money(new BigDecimal("1000"), Currency.USD));

        assertEquals(new BigDecimal("1000"), account.getBalance().getAmount());
    }

    @Test
    void testClosedStateBlocksAllOperations() {
        Account account = new SavingsAccount("SAV-001");
        account.close();

        assertThrows(IllegalStateException.class, () ->
                account.deposit(new Money(new BigDecimal("1000"), Currency.USD)));
    }
}