package jp.techacademy.daiki.ono.qa_app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.list_question_detail.view.*
import kotlin.math.log

class QuestionDetailListAdapter(context: Context, private val mQustion: Question) : BaseAdapter() {
    companion object {
        private val TYPE_QUESTION = 0
        private val TYPE_ANSWER = 1
    }
    private lateinit var mAdapter: QuestionDetailListAdapter
    private lateinit var mAnswerRef: DatabaseReference
    private lateinit var mDataBaseReference: DatabaseReference
    private lateinit var mAuth: FirebaseAuth



    private var mLayoutInflater: LayoutInflater? = null

    init {
        mLayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return 1 + mQustion.answers.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            TYPE_QUESTION
        } else {
            TYPE_ANSWER
        }
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Any {
        return mQustion
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var convertView = view
        mDataBaseReference = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()

        if (getItemViewType(position) == TYPE_QUESTION) {
            val user = FirebaseAuth.getInstance().currentUser
            if (convertView == null) {
                convertView = mLayoutInflater!!.inflate(R.layout.list_question_detail, parent, false)!!
            }
            val body = mQustion.body
            val name = mQustion.name

            val bodyTextView = convertView.bodyTextView as TextView
            bodyTextView.text = body

            val nameTextView = convertView.nameTextView as TextView
            nameTextView.text = name

            val bytes = mQustion.imageBytes
            if (bytes.isNotEmpty()) {
                val image = BitmapFactory.decodeByteArray(bytes, 0, bytes.size).copy(Bitmap.Config.ARGB_8888, true)
                val imageView = convertView.findViewById<View>(R.id.imageView) as ImageView
                imageView.setImageBitmap(image)
            }
            val favoriteButton=convertView.favoriteButton as Button
            if(user==null){
                favoriteButton.visibility=View.INVISIBLE
            }else{//ログインしているとき
                favoriteButton.visibility=View.VISIBLE
            }

            favoriteButton.setOnClickListener {
                val user = mAuth.currentUser
                val questionUid=mQustion.questionUid
                val genre=mQustion.genre.toString()


                val favoriteRef = mDataBaseReference.child(FaivoritesPATH).child(user!!.uid)
                val deleteRef = mDataBaseReference.child(FaivoritesPATH).child(user!!.uid)
                val data = HashMap<String, String>()
                data["questionUid"]=questionUid
                data["genre"]=genre

                favoriteRef.addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val idata = snapshot.value as Map<*, *>?
                        Log.d("text",idata.toString())
                        if(idata==null) {
                            favoriteRef.push().setValue(data)
                        }
                    }



                    override fun onCancelled(firebaseError: DatabaseError) {}
                })
                //mDataBaseReference.child(FaivoritesPATH).child(user!!.uid).get()
                //favoriteRef.push().setValue(data)

                    //favoriteRef.setValue(null)
                //favoriteRef.removeValue()

            }
        } else {
            if (convertView == null) {
                convertView = mLayoutInflater!!.inflate(R.layout.list_answer, parent, false)!!
            }

            val answer = mQustion.answers[position - 1]
            val body = answer.body
            val name = answer.name

            val bodyTextView = convertView.bodyTextView as TextView
            bodyTextView.text = body

            val nameTextView = convertView.nameTextView as TextView
            nameTextView.text = name
        }



        return convertView
    }
}