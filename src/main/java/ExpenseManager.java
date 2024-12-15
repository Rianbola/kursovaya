import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExpenseManager {
    private List<Expense> expenses = new ArrayList<>();

    // Метод для добавления расхода
    public void addExpense(double amount, String category, Date date) {
        expenses.add(new Expense(amount, category, date));
    }

    // Новый метод для получения всех расходов
    public List<Expense> getExpenses() {
        return new ArrayList<>(expenses); // Возвращаем копию списка, чтобы предотвратить внешние изменения
    }

    // Метод для проверки, что дата валидна
    public static boolean isValidDate(Date date) {
        if (date == null) {
            return false; // Если дата null, сразу возвращаем false
        }

        Calendar now = Calendar.getInstance();
        Calendar inputDate = Calendar.getInstance();
        inputDate.setTime(date);

        // Проверяем, чтобы дата не была в будущем
        if (inputDate.after(now)) {
            return false;
        }

        // Проверяем, что дата в пределах текущего месяца
        int currentYear = now.get(Calendar.YEAR);
        int currentMonth = now.get(Calendar.MONTH);

        int inputYear = inputDate.get(Calendar.YEAR);
        int inputMonth = inputDate.get(Calendar.MONTH);

        if (inputYear == currentYear && inputMonth == currentMonth) {
            return true;
        }

        // Проверяем, что дата в пределах предыдущего месяца
        now.add(Calendar.MONTH, -1); // Переходим на предыдущий месяц
        int previousYear = now.get(Calendar.YEAR);
        int previousMonth = now.get(Calendar.MONTH);

        return inputYear == previousYear && inputMonth == previousMonth;
    }


    // Метод для парсинга даты из строки
    public static Date parseDate(String dateStr) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            return format.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    // Метод для отображения статистики расходов
    public void showStatistics(Scanner scanner) {
        double totalExpenses = 0;
        Map<String, Double> categoryTotals = new HashMap<>();

        // Вычисляем общую сумму и по категориям
        for (Expense expense : expenses) {
            totalExpenses += expense.getAmount();
            categoryTotals.put(expense.getCategory(),
                    categoryTotals.getOrDefault(expense.getCategory(), 0.0) + expense.getAmount());
        }

        // Выбор статистики по категории
        String choice = "";
        while (!choice.equals("д") && !choice.equals("н")) {
            System.out.println("Хотите посмотреть статистику по конкретной категории? (д/н)");
            choice = scanner.nextLine().trim().toLowerCase();

            if (!choice.equals("д") && !choice.equals("н")) {
                System.out.println("Неверный выбор, нажмите 'д' (да) или 'н' (нет).");
            }
        }

        if (choice.equals("д")) {
            // Выводим доступные категории, которые содержат траты
            System.out.println("Выберите категорию:");
            List<String> availableCategories = new ArrayList<>();
            for (String category : categoryTotals.keySet()) {
                availableCategories.add(category);
                System.out.println(availableCategories.size() + ". " + category);
            }

            int selectedCategory = 0;
            while (selectedCategory < 1 || selectedCategory > availableCategories.size()) {
                try {
                    selectedCategory = scanner.nextInt();
                    scanner.nextLine(); // consume newline character
                    if (selectedCategory < 1 || selectedCategory > availableCategories.size()) {
                        System.out.println("Ошибка: неверный выбор.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Ошибка: введите число для выбора категории.");
                    scanner.nextLine(); // consume invalid input
                }
            }

            String selectedCategoryName = availableCategories.get(selectedCategory - 1);
            System.out.println("Статистика по категории: " + selectedCategoryName);
            double categoryTotal = categoryTotals.get(selectedCategoryName);
            double categoryPercentage = (categoryTotal / totalExpenses) * 100;
            System.out.println(selectedCategoryName + ": " + categoryTotal + " (" + String.format("%.2f", categoryPercentage) + "%)");
        }

        // Выводим общие расходы
        System.out.println("Общие расходы: " + totalExpenses);
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            double percentage = (entry.getValue() / totalExpenses) * 100;
            System.out.println(entry.getKey() + ": " + entry.getValue() + " (" + String.format("%.2f", percentage) + "%)");
        }
    }

    // Метод для выбора пути для сохранения отчета
    public static String chooseFilePath() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Выберите место для сохранения отчета");
        fileChooser.setSelectedFile(new File("Отчет.xlsx"));
        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        }
        return null;
    }

    // Метод для генерации Excel отчета
// Метод для генерации Excel отчета
    public void generateExcelReport(String filePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Отчет");

            // Стиль для заголовков
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Стиль для строки с общими расходами (зелёный фон)
            CellStyle greenStyle = workbook.createCellStyle();
            greenStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            greenStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            greenStyle.setAlignment(HorizontalAlignment.CENTER);

            // Создаем заголовок
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Категория");
            headerRow.createCell(1).setCellValue("Сумма");
            headerRow.createCell(2).setCellValue("Дата");
            headerRow.createCell(3).setCellValue("Процент от общей суммы");

            for (int i = 0; i <= 3; i++) {
                headerRow.getCell(i).setCellStyle(headerStyle);
            }

            // Вычисляем общую сумму расходов
            double totalAmount = expenses.stream().mapToDouble(Expense::getAmount).sum();

            if (totalAmount == 0) {
                System.out.println("Ошибка: Список расходов пуст, отчет не может быть создан.");
                return; // Прекращаем создание отчета, если нет данных
            }

            // Сохранение данных
            int rowNum = 1;
            for (Expense expense : expenses) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(expense.getCategory());
                row.createCell(1).setCellValue(expense.getAmount());
                row.createCell(2).setCellValue(new SimpleDateFormat("dd.MM.yyyy").format(expense.getDate()));
                row.createCell(3).setCellValue((expense.getAmount() / totalAmount) * 100);
            }

            // Добавляем строку с общими расходами
            Row totalRow = sheet.createRow(rowNum);
            totalRow.createCell(0).setCellValue("Общие расходы");
            Cell totalAmountCell = totalRow.createCell(1);
            totalAmountCell.setCellValue(totalAmount);

            // Применяем зелёный стиль для всех ячеек строки
            for (int i = 0; i <= 1; i++) {
                totalRow.getCell(i).setCellStyle(greenStyle);
            }

            // Автоматическое изменение ширины столбцов
            for (int i = 0; i <= 3; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1000); // Дополнительная ширина
            }

            // Сохранение в файл
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }

            System.out.println("Отчет сохранён в файл: " + filePath);
        } catch (Exception e) {
            System.out.println("Ошибка при генерации отчета: " + e.getMessage());
        }
    }
}