package beenet.sv.splynx_tas.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import beenet.sv.splynx_tas.R;

public class ServiceListAdapter extends RecyclerView.Adapter<ServiceListAdapter.ViewHolder> {

    private List<ServiceList> mData;
    private LayoutInflater mInflater;
    private Context context;

    final ServiceListAdapter.OnItemClickListener listener; //Evento on click
    public interface  OnItemClickListener {
        Void onItemClick(ServiceList item);
    }

    public ServiceListAdapter(List<ServiceList> itemList,Context context,ServiceListAdapter.OnItemClickListener listener){
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = itemList;
        this.listener = listener;
    }

    @Override
    public int getItemCount(){return mData.size();}

    @Override
    public ServiceListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent , int viewType){
        View view = mInflater.inflate(R.layout.list_internet_package,null);
        return new ServiceListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ServiceListAdapter.ViewHolder holder , final  int position){
        holder.bindData(mData.get(position));
    }

    public void setItems(List<ServiceList> items){mData = items;}

    public class ViewHolder extends  RecyclerView.ViewHolder{
        ImageView iconImage;
        TextView internet_package , internet_price, id_package;
        ViewHolder(View itemView){
            super(itemView);
            iconImage = itemView.findViewById(R.id.iconImageView);
            internet_package = itemView.findViewById(R.id.txt_navigation_package);
            internet_price = itemView.findViewById(R.id.txt_price);
            id_package = itemView.findViewById(R.id.id_package);
        }

        void bindData(final  ServiceList item){
            internet_package.setText(item.getService_Title());
            internet_price.setText(item.getService_price());
            id_package.setText(item.getService_Id());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

}
