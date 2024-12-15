import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class ExpenseManagerTest {
    private ExpenseManager expenseManager;

    @BeforeEach
    public void setUp() {
        expenseManager = new ExpenseManager();
    }

    // Тест метода addExpense
    @Test
    public void testAddExpense() {
        expenseManager.addExpense(100.50, "Транспорт", new Date());
        assertEquals(1, expenseManager.getExpenses().size(), "Размер списка должен быть равен 1");
    }

    // Тест метода isValidDate (дата в пределах текущего месяца)
    @Test
    public void testIsValidDate_CurrentMonth() {
        Date validDate = new Date(); // Текущая дата
        assertTrue(ExpenseManager.isValidDate(validDate), "Дата текущего месяца должна быть валидной");
    }

    // Тест метода isValidDate (дата месяца назад)
    @Test
    public void testIsValidDate_PreviousMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1); // Отнимаем месяц
        calendar.set(Calendar.DAY_OF_MONTH, 15); // Устанавливаем середину месяца
        Date dateLastMonth = calendar.getTime();

        assertTrue(ExpenseManager.isValidDate(dateLastMonth), "Дата месяца назад должна быть валидной");
    }

    // Тест метода isValidDate (будущая дата)
    @Test
    public void testIsValidDate_FutureDate() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        Date futureDate = sdf.parse("31.12.3000");

        assertFalse(ExpenseManager.isValidDate(futureDate), "Будущая дата должна быть невалидной");
    }

    // Тест метода isValidDate (дата больше чем месяц назад)
    @Test
    public void testIsValidDate_OlderThanOneMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -2); // Отнимаем два месяца
        calendar.set(Calendar.DAY_OF_MONTH, 15); // Устанавливаем середину месяца
        Date dateOlderThanOneMonth = calendar.getTime();

        assertFalse(ExpenseManager.isValidDate(dateOlderThanOneMonth), "Дата, которая старше чем на 1 месяц, должна быть невалидной");
    }

    // Тест метода isValidDate (некорректная дата)
    @Test
    public void testIsValidDate_InvalidDateFormat() {
        Date invalidDate = null; // Симуляция некорректной даты
        assertFalse(ExpenseManager.isValidDate(invalidDate), "Некорректная дата должна быть невалидной");
    }
}
