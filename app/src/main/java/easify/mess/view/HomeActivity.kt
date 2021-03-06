package easify.mess.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import easify.mess.R
import easify.mess.adapters.UserListAdapter
import easify.mess.databinding.ActivityHomeBinding
import easify.mess.model.Response
import easify.mess.model.User
import easify.mess.viewmodel.HomeViewModel

class HomeActivity : AppCompatActivity(), UserListAdapter.OnItemClick {

    private lateinit var viewModel: HomeViewModel
    private lateinit var dataBinding: ActivityHomeBinding
    private lateinit var userListAdapter: UserListAdapter
    lateinit var user: User

    private val userObserver = Observer<User> {
        user = it
    }

    private val toastMessageObserver = Observer<Response> {
        Toast.makeText(HomeActivity@this, it.message, Toast.LENGTH_LONG).show()
    }

    private val userListObserver = Observer<List<User>> { list ->
        list?.let {
            userListAdapter.update(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeDataBinding()
        initializeRecycleView()
        initializeViewModel()

        viewModel.getAllUsersExceptMe()
    }

    private fun initializeDataBinding() {
        dataBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_home)
    }

    private fun initializeRecycleView() {
        userListAdapter = UserListAdapter(this, arrayListOf())
        dataBinding.userList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = userListAdapter
        }
    }

    private fun initializeViewModel() {
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        viewModel.userData.observe(this, userObserver)
        viewModel.toastMessage.observe(this, toastMessageObserver)
        viewModel.userList.observe(this, userListObserver)
    }

    override fun onItemClick(user: User) {
        val i = Intent(this, MessageActivity::class.java)
        i.putExtra("user", user)
        startActivity(i)
    }
}