package jp.techacademy.daiki.ono.qa_app


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_question_detail.*
import kotlinx.android.synthetic.main.activity_question_detail.fab
import kotlinx.android.synthetic.main.app_bar_main.*


class QuestionDetailActivity : AppCompatActivity() {

    private lateinit var mQuestion: Question
    private lateinit var mAdapter: QuestionDetailListAdapter
    private lateinit var mAnswerRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDataBaseReference: DatabaseReference
    var jude=2


    private val mEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val map = dataSnapshot.value as Map<*, *>

            val answerUid = dataSnapshot.key ?: ""

            for (answer in mQuestion.answers) {
                // 同じAnswerUidのものが存在しているときは何もしない
                if (answerUid == answer.answerUid) {
                    return
                }
            }

            val body = map["body"] as? String ?: ""
            val name = map["name"] as? String ?: ""
            val uid = map["uid"] as? String ?: ""

            val answer = Answer(body, name, uid, answerUid)
            mQuestion.answers.add(answer)
            mAdapter.notifyDataSetChanged()
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {

        }

        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

        }

        override fun onCancelled(databaseError: DatabaseError) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_detail)
        mDataBaseReference = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()
        // 渡ってきたQuestionのオブジェクトを保持する
        val extras = intent.extras
        mQuestion = extras!!.get("question") as Question

        title = mQuestion.title


        // ListViewの準備
        mAdapter = QuestionDetailListAdapter(this, mQuestion)
        listView.adapter = mAdapter
        mAdapter.notifyDataSetChanged()



        val user = FirebaseAuth.getInstance().currentUser

        mDataBaseReference = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()
        /*
        val questionUid=mQuestion.questionUid
        val genre =mQuestion.genre.toString()
        val favoriteRef :DatabaseReference?= mDataBaseReference.child(FaivoritesPATH).child(user!!.uid).child(questionUid)
        val gdata = HashMap<String, String>()
        gdata["genre"]=genre

         */
        if(user==null){
            fab_favorite.hide()
        }else {
            val questionUid=mQuestion.questionUid
            val genre =mQuestion.genre.toString()
            val favoriteRef :DatabaseReference?= mDataBaseReference.child(FaivoritesPATH).child(user!!.uid).child(questionUid)
            val gdata = HashMap<String, String>()
            gdata["genre"]=genre
            favoriteRef!!.addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val idata = snapshot.value as Map<*, *>?

                    Log.d("text",idata.toString())
                    if(idata==null) {
                        jude=2
                        fab_favorite.setImageResource(R.drawable.ic_favorite)
                    }else if(idata!=null){
                        jude=3
                        fab_favorite.setImageResource(R.drawable.ic_favorite_m)
                    }
                }
                override fun onCancelled(firebaseError: DatabaseError) {}
            })

            fab_favorite.show()
        }
        /*
        favoriteRef!!.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val idata = snapshot.value as Map<*, *>?

                Log.d("text",idata.toString())
                if(idata==null) {
                    jude=2
                    fab_favorite.setImageResource(R.drawable.ic_favorite)
                }else if(idata!=null){
                    jude=3
                    fab_favorite.setImageResource(R.drawable.ic_favorite_m)
                }
            }
            override fun onCancelled(firebaseError: DatabaseError) {}
        })


*/



        fab.setOnClickListener {
            // ログイン済みのユーザーを取得する
            val user = FirebaseAuth.getInstance().currentUser

            if (user == null) {
                // ログインしていなければログイン画面に遷移させる
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)

            } else {

                // Questionを渡して回答作成画面を起動する
                val intent = Intent(applicationContext, AnswerSendActivity::class.java)
                intent.putExtra("question", mQuestion)
                startActivity(intent)
            }
        }

        val dataBaseReference = FirebaseDatabase.getInstance().reference
        mAnswerRef = dataBaseReference.child(ContentsPATH).child(mQuestion.genre.toString()).child(mQuestion.questionUid).child(AnswersPATH)
        mAnswerRef.addChildEventListener(mEventListener)
    }

    override fun onResume() {
        super.onResume()

        mDataBaseReference = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        /*
        val questionUid = mQuestion.questionUid
        val genre = mQuestion.genre.toString()
        val favoriteRef = mDataBaseReference.child(FaivoritesPATH).child(user!!.uid).child(questionUid)
        val gdata = HashMap<String, String>()
        gdata["genre"] = genre

         */
        if (user == null) {
            fab_favorite.hide()
        } else {
            val questionUid = mQuestion.questionUid
            val genre = mQuestion.genre.toString()
            val favoriteRef = mDataBaseReference.child(FaivoritesPATH).child(user!!.uid).child(questionUid)
            val gdata = HashMap<String, String>()
            gdata["genre"] = genre
            favoriteRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val idata = snapshot.value as Map<*, *>?

                    Log.d("text", idata.toString())
                    if (idata == null) {
                        jude = 2
                    } else {
                        jude = 3
                    }
                }

                override fun onCancelled(firebaseError: DatabaseError) {}
            })
            fab_favorite.setOnClickListener {
                if(jude%2==0){
                    favoriteRef.setValue(gdata)
                    fab_favorite.setImageResource(R.drawable.ic_favorite_m)
                }else{
                    favoriteRef.removeValue()
                    fab_favorite.setImageResource(R.drawable.ic_favorite)
                }
                jude++
            }
            fab_favorite.show()


/*
        favoriteRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val idata = snapshot.value as Map<*, *>?

                Log.d("text", idata.toString())
                if (idata == null) {
                    jude = 2
                } else {
                    jude = 3
                }
            }

            override fun onCancelled(firebaseError: DatabaseError) {}
        })
*/


    }
        /*
        fab_favorite.setOnClickListener {
           if(jude%2==0){
               favoriteRef.setValue(gdata)
               fab_favorite.setImageResource(R.drawable.ic_favorite_m)
           }else{
               favoriteRef.removeValue()
               fab_favorite.setImageResource(R.drawable.ic_favorite)
           }
            jude++
        }
*/

    }
}