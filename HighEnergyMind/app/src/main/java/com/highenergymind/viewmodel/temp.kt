//package com.highenergymind.viewmodel
//
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.RadioButton
//import androidx.databinding.ObservableBoolean
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import com.example.plazapalm.R
//import com.example.plazapalm.databinding.SignupFragmentBinding
//import com.example.plazapalm.pref.PreferenceFile
//import com.example.plazapalm.utils.CommonMethods
//import com.google.android.gms.tasks.Task
//import com.google.firebase.FirebaseApp
//import com.google.firebase.messaging.FirebaseMessaging
//import dagger.hilt.android.AndroidEntryPoint
//import javax.inject.Inject
//
//@AndroidEntryPoint
//class SignUpFragment : Fragment(R.layout.signup_fragment) {
//    private var binding: SignupFragmentBinding ? = null
//    var isclicked=ObservableBoolean(false)
//    private val viewModel: SignupVM by viewModels()
//
//    @Inject
//    lateinit var pref :PreferenceFile
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//
//    ): View? {
//
//        binding = SignupFragmentBinding.inflate(layoutInflater)
//
//        FirebaseApp.initializeApp(CommonMethods.context)
//        FirebaseMessaging.getInstance().token.addOnCompleteListener { task: Task<String> ->
//            if (!task.isSuccessful) {
//                return@addOnCompleteListener
//            }
//
//            viewModel.sendFirebaseSignUpToken.set(task.result)
//            pref.saveFirebaseToken(task.result)
//
//            Log.i("PUSH_TOKENSIGNUP---", "local->> : "+ viewModel.sendFirebaseSignUpToken.get())
//            Log.i("PUSH_TOKENSIGNUP---", "pushToken-->>> "+ task.result)
//
//        }
//
//        return binding?.root
//
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        binding?.vm = viewModel
//    }
//
//}
//
//
//
//
