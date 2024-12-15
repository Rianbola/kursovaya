import java.util.InputMismatchException;
import java.util.Scanner;

public class aaa {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ExpenseManager expenseManager = new ExpenseManager();

        while (true) {
            // Показать меню
            System.out.println("Выберите действие:");
            System.out.println("1. Добавить расход");
            System.out.println("2. Показать статистику");
            System.out.println("3. Сгенерировать отчет в Excel");
            System.out.println("4. Выход");

            int choice = 0;
            boolean validChoice = false;
            while (!validChoice) {
                try {
                    choice = scanner.nextInt();
                    scanner.nextLine(); // consume newline character
                    if (choice < 1 || choice > 4) {
                        System.out.println("Ошибка: выберите действие от 1 до 4.");
                    } else {
                        validChoice = true;
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Ошибка: введите номер действия (1-4).");
                    scanner.nextLine(); // consume invalid input
                }
            }

            if (choice == 1) {
                // Добавление расхода
                double amount = 0;
                boolean validAmount = false;
                while (!validAmount) {
                    System.out.print("Введите сумму: ");
                    String amountStr = scanner.nextLine();
                    try {
                        amount = Double.parseDouble(amountStr);
                        if (amount < 0.1) {
                            System.out.println("Ошибка: минимальная сумма расхода 0.1.");
                        } else {
                            validAmount = true;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка: введите число для суммы.");
                    }
                }

                // Выбор категории
                System.out.println("Выберите категорию:");
                String[] categories = {
                        "Страховка", "Коммунальные услуги", "Автоуслуги", "Транспорт", "Остальное",
                        "Образование", "Налоги", "Продукты", "Здоровье", "Топливо",
                        "Жилищные расходы", "Развлечения", "Путешествия"
                };
                for (int i = 0; i < categories.length; i++) {
                    System.out.println((i + 1) + ". " + categories[i]);
                }
                int categoryIndex = -1;
                while (categoryIndex < 0 || categoryIndex >= categories.length) {
                    try {
                        categoryIndex = scanner.nextInt() - 1; // Считываем выбор категории
                        scanner.nextLine();  // consume newline character
                        if (categoryIndex < 0 || categoryIndex >= categories.length) {
                            System.out.println("Ошибка: неверный выбор категории. Попробуйте снова.");
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("Ошибка: введите номер для выбора категории.");
                        scanner.nextLine();  // consume invalid input
                    }
                }
                String category = categories[categoryIndex];

                // Ввод даты
                String dateStr;
                boolean validDate = false;
                java.util.Date date = null;
                while (!validDate) {
                    System.out.print("Введите дату (дд.MM.гггг, например 12.12.2024): ");
                    dateStr = scanner.nextLine();
                    date = ExpenseManager.parseDate(dateStr);
                    if (date != null && ExpenseManager.isValidDate(date)) {
                        validDate = true;
                    } else {
                        System.out.println("Ошибка: дата должна быть в пределах текущего или прошлого месяца. Попробуйте снова.");
                    }
                }

                // Добавляем расход
                expenseManager.addExpense(amount, category, date);

            } else if (choice == 2) {
                // Показать статистику
                expenseManager.showStatistics(scanner);
            } else if (choice == 3) {
                // Сгенерировать отчет в Excel
                String filePath = ExpenseManager.chooseFilePath();  // Окно выбора пути
                if (filePath != null) {
                    expenseManager.generateExcelReport(filePath);
                } else {
                    System.out.println("Путь не был выбран. Отчет не сохранен.");
                }
            } else if (choice == 4) {
                // Выход
                break;
            }
        }

        scanner.close();
    }
}
