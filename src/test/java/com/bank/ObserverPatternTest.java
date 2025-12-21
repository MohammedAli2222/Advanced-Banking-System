package com.bank;

import com.bank.core.Account;
import com.bank.core.SavingsAccount;
import com.bank.notifications.NotificationLogger;
import com.bank.utils.Currency;
import com.bank.utils.Money;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class ObserverPatternTest {

    @Test
    void testNotificationOnDeposit() {
        Account account = new SavingsAccount("SAV-001");
        NotificationLogger logger = new NotificationLogger();
        account.addObserver(logger);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        account.deposit(new Money(new BigDecimal("1000"), Currency.USD));

        String output = out.toString();
        assertTrue(output.contains("[LOG]"));
        assertTrue(output.contains("Deposit successful"));
        assertTrue(output.contains("SAV-001"));
    }
}