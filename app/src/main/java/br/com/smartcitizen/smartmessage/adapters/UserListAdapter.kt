package br.com.smartcitizen.smartmessage.adapters

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import br.com.smartcitizen.smartmessage.R
import br.com.smartcitizen.smartmessage.databinding.HolderUserBinding
import br.com.smartcitizen.smartmessage.model.User

class UserListAdapter(private var listener: OnItemClick, private var userList: ArrayList<User>) :
    RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {

    class UserViewHolder(var view: HolderUserBinding) :
        RecyclerView.ViewHolder(view.root)

    interface OnItemClick {
        fun onItemClick(user: User)
    }

    fun update(newRoutes: List<User>) {
        userList.clear()
        userList.addAll(newRoutes)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<HolderUserBinding>(
            inflater,
            R.layout.holder_user,
            parent,
            false
        )
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]

        holder.itemView.setOnClickListener {
            listener.onItemClick(user)
        }

        if ( user.highlighted == 1 ) {
            holder.view.userName.setTypeface(holder.view.userName.typeface, Typeface.BOLD)
            holder.view.userPhone.setTypeface(holder.view.userPhone.typeface, Typeface.BOLD)
        } else {
            holder.view.userName.setTypeface(holder.view.userName.typeface, Typeface.NORMAL)
            holder.view.userPhone.setTypeface(holder.view.userPhone.typeface, Typeface.NORMAL)
        }

        if ( user.connected == 1 ) {
            holder.view.connectedSign.setBackgroundResource(R.drawable.green_circle)
        } else {
            holder.view.connectedSign.setBackgroundResource(R.drawable.red_circle)
        }

        if ( user.typing == 1 ) {
            holder.view.typing.visibility = View.VISIBLE
        } else {
            holder.view.typing.visibility = View.GONE
        }

        holder.view.user = user
    }

    override fun getItemCount() = userList.size
}