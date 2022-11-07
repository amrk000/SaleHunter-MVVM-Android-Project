package amrk000.salehunter.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName="bc")

public class BcRecord {

    @ColumnInfo(name="product")
    private String product;

    @PrimaryKey
    @ColumnInfo(name="pk")
    private int pk;

    @ColumnInfo(name="barcode")
    private String barcode;

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public int getPk() {
        return pk;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getBarcode() {
        return barcode;
    }
}
