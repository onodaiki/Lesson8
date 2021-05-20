package jp.techacademy.daiki.ono.qa_app

// findViewById()を呼び出さずに該当Viewを取得するために必要となるインポート宣言
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.database.*
import kotlinx.android.synthetic.main.content_main.*

class FavoriteActivity : AppCompatActivity() {
    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var mQuestionArrayList: ArrayList<Question>
    private lateinit var mAdapter: QuestionsListAdapter
   // private lateinit var mAuth: FirebaseAuth
    private var mFavoriteRef:DatabaseReference?=null
    private var mGenreRef: DatabaseReference? = null

    private val mEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {

            val map = dataSnapshot.value as Map<String, String>
            /*
            val title = map["title"] ?: ""
            val body = map["body"] ?: ""
            val name = map["name"] ?: ""
            val uid = map["uid"] ?: ""

            val imageString = map["image"] ?: ""
            val bytes =
                if (imageString.isNotEmpty()) {
                    Base64.decode(imageString, Base64.DEFAULT)
                } else {
                    byteArrayOf()
                }
*/
            val genre=map["genre"]?:""
            val questionUid=dataSnapshot.key?:""
            Log.d("genre",genre)
            Log.d("questionUid",questionUid)

            mGenreRef=mDatabaseReference.child(ContentsPATH).child(genre).child(questionUid)
            mGenreRef!!.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val map = snapshot.value as Map<String, String>
                    if(map!=null){
                    val title = map["title"] ?: ""
                    val body = map["body"] ?: ""
                    val name = map["name"] ?: ""
                    val uid = map["uid"] ?: ""
                    val imageString = map["image"] ?: ""
                    val bytes =
                        if (imageString.isNotEmpty()) {
                            Base64.decode(imageString, Base64.DEFAULT)
                        } else {
                            byteArrayOf()
                        }
                        val answerArrayList = ArrayList<Answer>()
                        val answerMap = map["answers"] as Map<String, String>?
                        if (answerMap != null) {
                            for (key in answerMap.keys) {
                                val temp = answerMap[key] as Map<String, String>
                                val answerBody = temp["body"] ?: ""
                                val answerName = temp["name"] ?: ""
                                val answerUid = temp["uid"] ?: ""
                                val answer = Answer(answerBody, answerName, answerUid, key)
                                answerArrayList.add(answer)
                            }
                        }

                        //val favoriteMap=map["favorites"] as Map<String, String>
                        // val questionUid = favoriteMap["questionUid"]?:""
                        // val favorite =Favorite(questionUid,dataSnapshot.key ?:"")

                        val question = Question(title, body, name, uid, dataSnapshot.key ?: "",
                            genre.toInt(), bytes, answerArrayList)


                        mQuestionArrayList.add(question)
                        //mFavoriteArrayList.add(favorite)
                        mAdapter.notifyDataSetChanged()
                    }



                }
                override fun onCancelled(firebaseError: DatabaseError) {}
            })

/*
            val answerArrayList = ArrayList<Answer>()
            val answerMap = map["answers"] as Map<String, String>?
            if (answerMap != null) {
                for (key in answerMap.keys) {
                    val temp = answerMap[key] as Map<String, String>
                    val answerBody = temp["body"] ?: ""
                    val answerName = temp["name"] ?: ""
                    val answerUid = temp["uid"] ?: ""
                    val answer = Answer(answerBody, answerName, answerUid, key)
                    answerArrayList.add(answer)
                }
            }

            //val favoriteMap=map["favorites"] as Map<String, String>
            // val questionUid = favoriteMap["questionUid"]?:""
            // val favorite =Favorite(questionUid,dataSnapshot.key ?:"")

            val question = Question(title, body, name, uid, dataSnapshot.key ?: "",
                mGenre, bytes, answerArrayList)


            mQuestionArrayList.add(question)
            //mFavoriteArrayList.add(favorite)
            mAdapter.notifyDataSetChanged()
            */

        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
            val map = dataSnapshot.value as Map<String, String>

            // 変更があったQuestionを探す
            for (question in mQuestionArrayList) {
                if (dataSnapshot.key.equals(question.questionUid)) {
                    // このアプリで変更がある可能性があるのは回答（Answer)のみ
                    question.answers.clear()
                    val answerMap = map["answers"] as Map<String, String>?
                    if (answerMap != null) {
                        for (key in answerMap.keys) {
                            val temp = answerMap[key] as Map<String, String>
                            val answerBody = temp["body"] ?: ""
                            val answerName = temp["name"] ?: ""
                            val answerUid = temp["uid"] ?: ""
                            val answer = Answer(answerBody, answerName, answerUid, key)
                            question.answers.add(answer)
                        }
                    }

                    mAdapter.notifyDataSetChanged()
                }
            }
        }

        override fun onChildRemoved(p0: DataSnapshot) {

        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {

        }

        override fun onCancelled(p0: DatabaseError) {

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_activity)

        listView.setOnItemClickListener{parent, view, position, id ->
            // Questionのインスタンスを渡して質問詳細画面を起動する
            val intent = Intent(applicationContext, QuestionDetailActivity::class.java)
            intent.putExtra("question", mQuestionArrayList[position])
            startActivity(intent)
        }


        mDatabaseReference = FirebaseDatabase.getInstance().reference

        // ListViewの準備
        mAdapter = QuestionsListAdapter(this)
        mQuestionArrayList = ArrayList<Question>()
        mAdapter.notifyDataSetChanged()
        mQuestionArrayList.clear()
        mAdapter.setQuestionArrayList(mQuestionArrayList)
        listView.adapter = mAdapter
        val user = FirebaseAuth.getInstance().currentUser
        /*
        if (mGenreRef != null) {
            mGenreRef!!.removeEventListener(mEventListener)
        }
        */
        mFavoriteRef=mDatabaseReference.child(FaivoritesPATH).child(user!!.uid)
        mFavoriteRef!!.addChildEventListener(mEventListener)
        //mGenreRef=mDatabaseReference.child(ContentsPATH).child(user!!.uid)
        //mGenreRef!!.addChildEventListener(mEventListener)

    }

    override fun onResume() {
        super.onResume()

    }
}