package amrk000.salehunter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import amrk000.salehunter.R;
import amrk000.salehunter.model.SettingsItemModel;

public class SettingsListAdapter extends RecyclerView.Adapter<SettingsListAdapter.DataViewHolder> {
    private ArrayList<SettingsItemModel> data;
    private RecyclerView recyclerView;
    private Context context;

    private SettingsListAdapter.ItemClickListener itemClickListener;

    public interface ItemClickListener {
        void onItemClicked(int position);
    }

    public void setItemClickListener(SettingsListAdapter.ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    public SettingsListAdapter(Context context, ArrayList<SettingsItemModel> data,RecyclerView recyclerView){
        this.context = context;
        this.recyclerView = recyclerView;
        this.data = data;
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder{
        TextView label, currentSettingValue;

        public DataViewHolder(View view) {
            super(view);
            label = view.findViewById(R.id.settings_list_label);
            currentSettingValue = view.findViewById(R.id.settings_list_currentSetting);
        }
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.settings_list_item_layout, parent, false);

        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        holder.label.setText(data.get(position).getName());
        holder.currentSettingValue.setText(data.get(position).getValue());

        holder.itemView.setOnClickListener(view ->{
            itemClickListener.onItemClicked(position);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}
