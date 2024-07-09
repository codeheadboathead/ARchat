package com.example.archat.activities.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.archat.Models.chatMessage;
import com.example.archat.databinding.ItemContainerRecivedMessageBinding;
import com.example.archat.databinding.ItemContainerSentMessageBinding;
import com.example.archat.databinding.ItemContainerUserBinding;

import java.util.List;

public class chatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<chatMessage> chatMessages;
    private final String senderId;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    public chatAdapter(List<chatMessage> chatMessages, String senderId) {
        this.chatMessages = chatMessages;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         if (viewType == VIEW_TYPE_SENT){
             return new sentMessageViewHolder(
                     ItemContainerSentMessageBinding.inflate(
                             LayoutInflater.from(parent.getContext()),
                             parent,
                             false
                     )
             );
         } else {
             return new RecivedMessageViewHolder(
                     ItemContainerRecivedMessageBinding.inflate(
                             LayoutInflater.from(parent.getContext()),
                             parent,
                             false
                     )
             );
         }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            ((sentMessageViewHolder) holder).setData(chatMessages.get(position));
        }
        else {
            ((RecivedMessageViewHolder) holder).setData(chatMessages.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).senderID.equals(senderId)){
            return VIEW_TYPE_SENT;
        }
        else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    static class sentMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerSentMessageBinding binding;


        sentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding){
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;

        }
        void setData(chatMessage chatMessage){
            binding.textmessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);


        }
    }


    static class RecivedMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerRecivedMessageBinding binding;

        RecivedMessageViewHolder(ItemContainerRecivedMessageBinding itemContainerRecivedMessageBinding) {
            super(itemContainerRecivedMessageBinding.getRoot());
            binding = itemContainerRecivedMessageBinding;
        }

        void setData(chatMessage chatMessage){
            binding.textmessage.setText(chatMessage.message);
            binding.textDateTime.setText(chatMessage.dateTime);

        }
    }
}
