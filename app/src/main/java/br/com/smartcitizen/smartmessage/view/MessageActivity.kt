package br.com.smartcitizen.smartmessage.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.smartcitizen.smartmessage.R
import br.com.smartcitizen.smartmessage.adapters.MessageAdapter
import br.com.smartcitizen.smartmessage.databinding.ActivityMessageBinding
import br.com.smartcitizen.smartmessage.model.Message
import br.com.smartcitizen.smartmessage.model.Response
import br.com.smartcitizen.smartmessage.model.User
import br.com.smartcitizen.smartmessage.viewmodel.MessageViewModel

import java.util.*


class MessageActivity : AppCompatActivity(), MessageAdapter.OnItemClick {

    private lateinit var dataBinding: ActivityMessageBinding
    private lateinit var viewModel: MessageViewModel
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var user: User
    private lateinit var to: User
    private lateinit var message: Message

    private var timer: Timer = Timer()
    private val DELAY: Long = 1000

    private val userObserver = Observer<User> {
        this.user = it
        initializeRecycleView()
    }

    private val messageObserver = Observer<Message> { data: Message ->
        data.let {
            this.message = it
            dataBinding.message = this.message
        }
    }

    private val messagesObserver = Observer<List<Message>> { data: List<Message> ->
        data.let {
            messageAdapter.updateMessages(it)
            dataBinding.messages.scrollToPosition(messageAdapter.itemCount - 1)
        }
    }

    private val toastMessageObserver = Observer<Response> {
        Toast.makeText(MessageActivity@ this, it.message, Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        initializeViewModel()
        setToUser()
        initializeDataBinding()
    }

    private fun initializeRecycleView() {
        messageAdapter = MessageAdapter(this, arrayListOf(), this, this.user)
        dataBinding.messages.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = messageAdapter
        }
    }

    private fun setToUser() {
        to = intent.extras!!.get("user") as User
        to.let {
            viewModel.setToUser(it)
            supportActionBar!!.title = to.usuario_nome
        }
    }

    private fun initializeDataBinding() {
        dataBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_message)

        dataBinding.sendMessage.setOnClickListener(View.OnClickListener {
            viewModel.sendMessageToApi()
        })

        dataBinding.messageContent.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    timer = Timer()
                    timer.schedule(object : TimerTask() {
                        override fun run() {
                            viewModel.finishedTyping()
                        }
                    }, DELAY)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                viewModel.startedTyping()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (timer != null)
                    timer.cancel()
            }
        })
    }

    private fun initializeViewModel() {
        viewModel = ViewModelProviders.of(this).get(MessageViewModel::class.java)
        viewModel.user.observe(this, userObserver)
        viewModel.message.observe(this, messageObserver)
        viewModel.messages.observe(this, messagesObserver)
        viewModel.toastMessage.observe(this, toastMessageObserver)
        viewModel.loadChat()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onItemDelete(chatId: Long) {

    }
}