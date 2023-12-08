package vn.vnpt.myapplication

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.vnpt.smartca.ConfigSDK
import com.vnpt.smartca.SmartCAEnvironment
import com.vnpt.smartca.SmartCALanguage
import com.vnpt.smartca.SmartCAResultCode
import com.vnpt.smartca.VNPTSmartCASDK
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class MainActivity : AppCompatActivity() {
    var VNPTSmartCA = VNPTSmartCASDK()
    lateinit var editTextTrans: EditText;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val btnActiveAccount = findViewById<Button>(R.id.btnActiveAccount)
        editTextTrans = findViewById(R.id.plain_text_input)

        val btnConfirmTrans = findViewById<Button>(R.id.btnConfirmTrans)
        val bntMainInfo = findViewById<Button>(R.id.btnMainInfo)


        val config = ConfigSDK()
        config.context = this
        config.partnerId = "VNPTSmartCAPartner-add1fb94-9629-49`47-b7d8-f2671b04c747"
        config.environment = SmartCAEnvironment.DEMO_ENV
        config.lang = SmartCALanguage.VI
        config.isFlutter = false
        VNPTSmartCA.initSDK(config)

        btnActiveAccount.setOnClickListener {
            getAuthentication()
        }
        bntMainInfo.setOnClickListener {
            getMainInfo()
        }
        btnConfirmTrans.setOnClickListener {
            getWaitingTransaction(editTextTrans.text.toString())
        }
    }

    private fun getMainInfo() {
        try {
            VNPTSmartCA.getMainInfo { result ->
                when (result.status) {
                    SmartCAResultCode.SUCCESS_CODE -> {
                        // Xử lý khi confirm thành công
                    }

                    else -> {
                        // Xử lý khi confirm thất bại
                    }
                }
            }
        } catch (ex: java.lang.Exception) {
            throw ex;
        }
    }

    private fun getAuthentication() {
        try {
            VNPTSmartCA.getAuthentication { result ->
                when (result.status) {
                    SmartCAResultCode.SUCCESS_CODE -> {
                        val obj: CallbackResult = Json.decodeFromString(
                            CallbackResult.serializer(), result.data.toString()
                        )
                        // SDK trả lại token, credential của khách hàng
                        // Đối tác tạo transaction cho khách hàng để lấy transId, sau đó gọi getWaitingTransaction
                        val token = obj.accessToken
                        val credentialId = obj.credentialId

                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Xác thực thành công")
                        builder.setMessage("CredentialId: $credentialId;\nAccessToken: $token")
                        builder.setPositiveButton(
                            "Close"
                        ) { dialog, _ -> dialog.dismiss() }
                        builder.show()
                    }

                    else -> {
                        // Xử lý lỗi
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Thông báo")
                        builder.setMessage("status: ${result.status}; statusDesc:  ${result.statusDesc}")
                        builder.setPositiveButton(
                            "Close"
                        ) { dialog, _ -> dialog.dismiss() }
                        builder.show()
                    }
                }
            }
        } catch (ex: Exception) {
            throw ex;
        }
    }

    private fun getWaitingTransaction(transId: String) {
        try {
            if (transId.isNullOrEmpty()) {
                editTextTrans.error = "Vui lòng điền Id giao dịch";
                return
            }

            VNPTSmartCA.getWaitingTransaction(transId) { result ->
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Thông báo")
                builder.setMessage("status: ${result.status}; statusDesc:  ${result.statusDesc}")
                builder.setPositiveButton(
                    "Close"
                ) { dialog, _ -> dialog.dismiss() }
                builder.show()

                when (result.status) {
                    SmartCAResultCode.SUCCESS_CODE -> {
                        // Xử lý khi thành công
                    }

                    else -> {
                        // Xử lý khi thất bại
                    }
                }

            }
        } catch (ex: Exception) {
            throw ex;
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        VNPTSmartCA.destroySDK();
    }
}

@Serializable
data class CallbackResult(
    val credentialId: String,
    val accessToken: String,
) : java.io.Serializable