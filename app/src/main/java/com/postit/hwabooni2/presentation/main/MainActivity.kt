package com.postit.hwabooni2.presentation.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.postit.hwabooni2.R
import com.postit.hwabooni2.model.Emotion
import com.postit.hwabooni2.model.FriendData
import com.postit.hwabooni2.presentation.detail.DetailActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicInteger

class MainActivity : AppCompatActivity() {
    private val TAG = "NewMainActivity"
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data = MutableLiveData(listOf<FriendData>())

        db.collection("SocialWorker").document("leehakyung").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val temp = mutableListOf<FriendData>()
                    val list = (task.result.get("follower") as? List<String>)
                    list?.let {
                        var cnt = AtomicInteger(list.size)
                        list.forEach {
                            db.collection("User").document(it).get().addOnCompleteListener {
                                if(task.isSuccessful){
                                    it.result.toObject<FriendData>()?.let {
                                        temp.add(it)
                                    }
                                }
                                if(cnt.decrementAndGet()==0){
                                    data.postValue(temp)
                                }
                            }
                        }
                    }
                }
            }

        setContent {
            MainScreen(data)
        }

    }

}

@Composable
fun MainScreen(
    data: LiveData<List<FriendData>>
) {
    val list by data.observeAsState()
    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(backgroundColor = colorResource(id = R.color.light_gray)) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "화분이")
                    }
                }
            },
            backgroundColor = colorResource(id = R.color.light_gray)
        ) {
            Column {
                TeacherCardView()
                FriendList(list = list ?: listOf())
            }
        }
    }
}

@Composable
fun FriendList(list: List<FriendData>) {

    Column {
        Row {
            Spacer(modifier = Modifier.width(22.dp))
            Text(
                text = "대상자들",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(vertical = 5.dp)
            )
        }
        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(list) {
                FriendCardView(data = it)
            }
        }
    }
}

@Composable
fun FriendCardView(data: FriendData) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .height(106.dp)
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .clickable { context.startActivity(DetailActivity.getIntent(context, data.id)) },
        elevation = 1.dp,
        shape = RoundedCornerShape(2.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(18.dp))
            AsyncImage(
                model = data.plantImage,
                contentDescription = "Profile Image",
                modifier = Modifier
                    .width(55.dp)
                    .height(55.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .shadow(4.dp)
            )
            Text(
                text = data.name,
                modifier = Modifier.padding(horizontal = 12.dp),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
                color = Color.Black
            )
            Image(
                painter = painterResource(id = Emotion.values()[data.emotion.toInt()].icon),
                modifier = Modifier
                    .width(55.dp)
                    .height(55.dp),
                contentDescription = "Emotion Image"
            )
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                Row {
                    IconButton(onClick = {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_DIAL,
                                Uri.parse("tel:${data.phone}")
                            )
                        )
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_telephone),
                            contentDescription = "Call Button",
                            modifier = Modifier
                                .width(40.dp)
                                .height(40.dp),
                            tint = Color.Green
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }
        }
    }
}

@Composable
fun TeacherCardView() {
    Card(
        modifier = Modifier
            .height(106.dp)
            .fillMaxWidth()
            .padding(start = 14.dp, top = 8.dp, bottom = 8.dp), elevation = 1.dp,
        shape = RoundedCornerShape(2.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(22.dp))
            Text(text = "이하경 선생님", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}