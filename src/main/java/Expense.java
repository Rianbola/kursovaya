import java.util.Date;

public class Expense {
    private double amount;
    private String category;
    private Date date;

    public Expense(double amount, String category, Date date) {
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public Date getDate() {
        return date;
    }

}
