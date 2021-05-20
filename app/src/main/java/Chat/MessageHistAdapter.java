package Chat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.streetteam.app.R;


public class MessageHistAdapter extends BaseAdapter {

    List<Message> messages = new ArrayList<Message>();
    Context context;

    public MessageHistAdapter(Context context) {
        this.context = context;
    }


    public void add(Message message) {
        this.messages.add(message);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        MessageViewHolder holder = new MessageViewHolder();
        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        Message message = messages.get(i);
        String[] split = message.getText().split("\\s\\s");
        convertView = messageInflater.inflate(R.layout.their_message, null);
        holder.avatar = (View) convertView.findViewById(R.id.avatar);
        holder.name = (TextView) convertView.findViewById(R.id.their_name);
        holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
        holder.messageTime = (TextView) convertView.findViewById(R.id.message_time);
        GradientDrawable drawable = (GradientDrawable) holder.avatar.getBackground();
        convertView.setTag(holder);
        setMsgText(holder, split, drawable);
        //name.setText(message.getMemberData().getName())holder;
        //drawable.setColor(Color.parseColor(message.getMemberData().getColor()));
        return convertView;
    }

    private void setMsgText(MessageViewHolder holder, String[] split, GradientDrawable drawable) {
        //0 имя 1 время 3 текст
        if (split.length > 1) {
            holder.messageBody.setText(split[4]);
            holder.messageTime.setText(split[2]);
            holder.name.setText(split[0]);
            drawable.setColor(Color.parseColor(split[1]));
        } else {
            holder.messageBody.setText(split[0]);
        }
    }

    static class MessageViewHolder {
        public View avatar;
        public TextView name;
        public TextView messageBody;
        public TextView messageTime;
    }
}

