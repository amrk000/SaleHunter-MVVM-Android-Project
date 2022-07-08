package api.software.salehunter.data.local;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import api.software.salehunter.model.BcRecord;

//Data Access object
@Dao
public interface myDao {

    @Query("select * from bc")
    public List<BcRecord> getData();

    @Query("select product from bc WHERE barcode = :productBarcode")
    public String lookUpProduct(String productBarcode);

}
