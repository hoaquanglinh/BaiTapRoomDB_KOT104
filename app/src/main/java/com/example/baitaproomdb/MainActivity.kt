package com.example.baitaproomdb

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.baitaproomdb.ui.theme.BaiTapRoomDBTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BaiTapRoomDBTheme {
                Scaffold(modifier = Modifier
                    .safeDrawingPadding()
                    .fillMaxSize()
                    .padding(16.dp)) { innerPadding ->
                    Greeting(
                        name = "Quản ly Sinh vien",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {

    val context = LocalContext.current
    val db = Room.databaseBuilder(
        context,
        StudentDB::class.java, "student-db"
    ).allowMainThreadQueries().build()

    // coroutine - da luong
    var listStudents by remember {
        mutableStateOf(db.studentDAO().getAll())
    }

    var hoTen by remember {
        mutableStateOf("")
    }
    var mssv by remember {
        mutableStateOf("")
    }
    var diemTB by remember {
        mutableStateOf("")
    }
    var daRaTruong by remember {
        mutableStateOf("")
    }

    var showDialogThongtinSV by remember { mutableStateOf(false) }

    var dialogMessage by remember {
        mutableStateOf("")
    }

    var idsv by remember {
        mutableStateOf(0)
    }

    if (showDialogThongtinSV) {
        val tatDialog = {
            showDialogThongtinSV = false
        }

        db.studentDAO().getStudent(idsv)?.let {
            ShowDialogStudentInfor(
                onConfirmation = tatDialog,
                dialogMessage = dialogMessage,
                sv = it,
                context = context,
                onDelete = {
                    db.studentDAO().delete(it)
                    listStudents = db.studentDAO().getAll()
                    tatDialog()
                }
            )
        }
    }

    Column (Modifier.fillMaxWidth()){
        Text(
            text = "Quan ly Sinh vien",
            style = MaterialTheme.typography.titleLarge
        )

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 16.dp)
        ){
            OutlinedTextField(
                value = hoTen,
                onValueChange = { hoTen = it},
                label = {
                    Text("Nhap ten")
                },
                modifier = Modifier.weight(1f))

            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = mssv,
                onValueChange = { mssv = it},
                label = {
                    Text("MSSV")
                },
                modifier = Modifier.weight(1f))
        }

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 16.dp)
        ){
            OutlinedTextField(
                value = diemTB,
                onValueChange = { diemTB = it },
                label = {
                    Text("Diem TB")
                },
                modifier = Modifier.weight(1f))

            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = daRaTruong,
                onValueChange = { daRaTruong = it},
                label = {
                    Text("Da ra truong")
                },
                modifier = Modifier.weight(1f))
        }

        Button(onClick = {
            if (hoTen.trim().equals("") || mssv.trim().equals("") || diemTB.isEmpty() || daRaTruong.isEmpty()){
                Toast.makeText(context,"Chua nhap du thong tin", Toast.LENGTH_SHORT).show()
            }else if (!isFloat(value = diemTB)){
                Toast.makeText(context,"Diem tb nhap chua dung", Toast.LENGTH_SHORT).show()
            }else if (10f < diemTB.toFloat() || diemTB.toFloat() < 0f){
                Toast.makeText(context,"Diem tb phai tu 0 - 10", Toast.LENGTH_SHORT).show()
            }else if (!isBoolean(daRaTruong)){
                Toast.makeText(context,"Da ra truong nhap 1 la 'Da ra' hoac 2 la 'Chua ra'", Toast.LENGTH_SHORT).show()
            }else {
                db.studentDAO().insert(
                    StudentModel(
                        hoten = hoTen,
                        mssv = mssv,
                        diemTB = diemTB.toFloat(),
                        daratruong = if(daRaTruong.equals("1")) true else false))
                listStudents = db.studentDAO().getAll()
                Toast.makeText(context,"Them sv moi thanh cong", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text(text = "Thêm SV")
        }

        Row (
            modifier = Modifier
                .fillMaxWidth().padding(0.dp,10.dp)
        ){
            Text(modifier = Modifier.weight(0.8f), text = "STT")
            Text(modifier = Modifier.weight(1.3f), text = "Name")
            Text(modifier = Modifier.weight(1f), text = "MSSV")
            Text(modifier = Modifier.weight(0.8f), text = "Point")
            Text(modifier = Modifier.weight(1f), text = "Status")
        }

        LazyColumn {

            items(listStudents) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable {
                            idsv = it.uid
                            dialogMessage = it.getThongtin()
                            showDialogThongtinSV = true

                        }
                ){
                    Text(modifier = Modifier.weight(0.5f), text = it.uid.toString())
                    Text(modifier = Modifier.weight(1.3f), text = it.hoten.toString())
                    Text(modifier = Modifier.weight(1.2f), text = it.mssv.toString())
                    Text(modifier = Modifier.weight(0.5f), text = it.diemTB.toString())
                    Text(modifier = Modifier.weight(1f), text =if(it.daratruong!!) "Da ra" else "Chưa ra")
                }
                Divider()
            }
        }
    }
}


@Composable
fun ShowDialogStudentInfor(
    onConfirmation: () -> Unit,
    dialogTitle: String = "Thong tin SV",
    dialogMessage: String,
    sv : StudentModel,
    context: Context,
    onDelete: (sv : StudentModel) -> Unit
) {

    Dialog(onDismissRequest = {}) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
            ),
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(dialogTitle, style =
                MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(20.dp))
                Text(dialogMessage, style =
                MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    Modifier.fillMaxWidth()
                ){
                    Button(
                        onClick = onConfirmation,
                        modifier = Modifier
                            .padding(10.dp)
                            .weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.DarkGray,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Okay")
                    }

                    Button(
                        onClick = {
                            onConfirmation()
                            onDelete(sv)
                            Toast.makeText(context, "Sinh vien cos mssv ${sv.mssv} da duoc xoa", Toast.LENGTH_SHORT).show()
                        }
                        ,
                        modifier = Modifier
                            .padding(10.dp)
                            .weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.DarkGray,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}

fun isFloat(value: String): Boolean {
    return try {
        value.toFloat()
        true
    } catch (e: NumberFormatException) {
        false
    }
}

fun isBoolean(value: String): Boolean {
    return value.equals("1", ignoreCase = true) || value.equals("2", ignoreCase = true)
}