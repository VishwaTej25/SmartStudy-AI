package com.example.smartstudy.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent() {

    val context = LocalContext.current

    val drawerState =
        rememberDrawerState(
            initialValue = DrawerValue.Closed
        )

    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,

        drawerContent = {

            ModalDrawerSheet {

                Spacer(Modifier.height(20.dp))

                Text(
                    "Smart Study AI 🚀",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                NavigationDrawerItem(
                    label = { Text("🏠 Home") },
                    selected = true,
                    onClick = {}
                )

                NavigationDrawerItem(
                    label = { Text("🏆 Leaderboard") },
                    selected = false,
                    onClick = {
                        Toast.makeText(
                            context,
                            "Leaderboard",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )

                NavigationDrawerItem(
                    label = { Text("⚙ Settings") },
                    selected = false,
                    onClick = {
                        Toast.makeText(
                            context,
                            "Settings",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )

                NavigationDrawerItem(
                    label = { Text("💎 Premium") },
                    selected = false,
                    onClick = {
                        Toast.makeText(
                            context,
                            "Premium",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )

                NavigationDrawerItem(
                    label = { Text("🚪 Logout") },
                    selected = false,
                    onClick = {
                        Toast.makeText(
                            context,
                            "Logout",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )

            }

        }

    ) {

        LazyColumn(

            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF050B1A),
                                Color(0xFF0A1B55)
                            )
                        )
                    )
                    .padding(16.dp)

        ) {

            item {


                Spacer(
                    Modifier.height(10.dp)
                )

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.SpaceBetween,

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Spacer(
                            Modifier.width(8.dp)
                        )

                        Column {

                            Text(
                                "Hello Vishwa 👋",
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight =
                                    FontWeight.Bold
                            )

                            Text(
                                "AI powered learning dashboard 🚀",
                                color = Color.Gray
                            )

                        }

                    }

                    Row {


                    }

                }

                Spacer(
                    Modifier.height(20.dp)
                )

                Card(
                    shape =
                        RoundedCornerShape(20.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                Color(0xFF8B3DFF)
                        )
                ) {

                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(24.dp),

                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {

                        Column {

                            Text(
                                "🔥 Study Streak",
                                color = Color.White
                            )

                            Spacer(
                                Modifier.height(10.dp)
                            )

                            Text(
                                "7 Days",
                                color = Color.White,
                                fontSize = 34.sp,
                                fontWeight =
                                    FontWeight.Bold
                            )

                            Text(
                                "Keep it up!",
                                color = Color.White
                            )

                        }

                        Box(
                            modifier =
                                Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Color(0xFF6F2BFF)
                                    ),

                            contentAlignment =
                                Alignment.Center
                        ) {

                            Text(
                                "🔥",
                                fontSize = 34.sp
                            )

                        }

                    }

                }

                Spacer(Modifier.height(25.dp))

                Card(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),

                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1B2235)
                    )

                ) {

                    Column(

                        modifier =
                            Modifier.padding(20.dp)

                    ) {

                        Text(
                            text = "👤 Vishwa",
                            color = Color.White,
                            style =
                                MaterialTheme.typography.headlineSmall
                        )

                        Spacer(
                            Modifier.height(10.dp)
                        )

                        Text(
                            text = "Department : CSE",
                            color = Color.LightGray
                        )

                        Text(
                            text = "Year : 4th Year",
                            color = Color.LightGray
                        )

                        Text(
                            text = "College : SIMATS",
                            color = Color.LightGray
                        )

                        Spacer(
                            Modifier.height(10.dp)
                        )

                        Text(
                            text="Keep learning 🚀",
                            color=Color(0xFF8B5CF6)
                        )

                    }

                }

                Spacer(
                    Modifier.height(16.dp)
                )

                Row(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),

                    horizontalArrangement =
                        Arrangement.SpaceBetween

                ) {

                    SmallStatCard(
                        "📚 Courses",
                        "4"
                    )

                    SmallStatCard(
                        "✅ Done",
                        "2"
                    )

                }

                Spacer(
                    Modifier.height(12.dp)
                )

                Row(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal=16.dp),

                    horizontalArrangement =
                        Arrangement.SpaceBetween

                ) {

                    SmallStatCard(
                        "🏆 Badges",
                        "15"
                    )

                    SmallStatCard(
                        "🔥 Streak",
                        "7"
                    )

                }

                Spacer(
                    Modifier.height(25.dp)
                )

                Text(
                    "Quick Access",
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(15.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {

                    QuickCard("Notes","📚")
                    QuickCard("Tasks","✅")
                    QuickCard("Practice","🎯")
                    QuickCard("Analytics","📈")

                }

                Spacer(Modifier.height(30.dp))

                SectionCard(
                    "AI Prediction Score",
                    "91% probability of scoring above A grade"
                )

                Spacer(Modifier.height(25.dp))

                SectionCard(
                    "Continue Learning",
                    "DSA + Java Interview Prep - 72%"                )

                Spacer(Modifier.height(25.dp))

                WeeklyGraph()

                Spacer(Modifier.height(25.dp))

                Text(
                    "Today's Plan",
                    color=Color.White,
                    fontSize=24.sp,
                    fontWeight=FontWeight.Bold
                )

                Spacer(
                    Modifier.height(10.dp)
                )

                PlanCard(
                    "Practice DSA Problems",
                    "11:00 AM"
                )

                Spacer(
                    Modifier.height(10.dp)
                )

                PlanCard(
                    "Java Interview Questions",
                    "02:00 PM"
                )

                Spacer(
                    Modifier.height(100.dp)
                )

            }

        }

    }

}

@Composable
fun QuickCard(
    title:String,
    emoji:String
){

    Card(
        modifier=
            Modifier
                .width(80.dp)
                .height(90.dp)
                .clickable {},

        colors=
            CardDefaults.cardColors(
                containerColor=
                    Color(0xFF1A2033)
            )
    ){

        Column(
            modifier=
                Modifier.fillMaxSize(),

            horizontalAlignment=
                Alignment.CenterHorizontally,

            verticalArrangement=
                Arrangement.Center
        ){

            Text(
                emoji,
                fontSize=25.sp
            )

            Spacer(
                Modifier.height(6.dp)
            )

            Text(
                title,
                color=Color.White
            )

        }

    }

}

@Composable
fun SectionCard(
    title:String,
    subtitle:String
){

    Card(
        colors=
            CardDefaults.cardColors(
                containerColor=
                    Color(0xFF1A2033)
            )
    ){

        Column(
            modifier=
                Modifier.padding(18.dp)
        ){

            Text(
                title,
                color=Color.White,
                fontSize=22.sp
            )

            Spacer(
                Modifier.height(8.dp)
            )

            LinearProgressIndicator(
                progress={0.85f},
                modifier=
                    Modifier.fillMaxWidth()
            )

            Spacer(
                Modifier.height(8.dp)
            )

            Text(
                subtitle,
                color=
                    Color(0xFF8B3DFF)
            )

        }

    }

}

@Composable
fun WeeklyGraph(){

    Card(
        colors=
            CardDefaults.cardColors(
                containerColor=
                    Color(0xFF1A2033)
            )
    ){

        Column(
            modifier=
                Modifier.padding(18.dp)
        ){

            Text(
                "Weekly Student Performance 📈",
                color=Color.White,
                fontSize=22.sp
            )

            Spacer(
                Modifier.height(20.dp)
            )

            Row(
                modifier=
                    Modifier.fillMaxWidth(),
                horizontalArrangement=
                    Arrangement.SpaceEvenly,
                verticalAlignment=
                    Alignment.Bottom
            ){

                GraphBar(65,"Mon")
                GraphBar(45,"Tue")
                GraphBar(95,"Wed")
                GraphBar(60,"Thu")
                GraphBar(100,"Fri")
                GraphBar(80,"Sat")
                GraphBar(75,"Sun")

            }

        }

    }

}

@Composable
fun GraphBar(value:Int,day:String){

    Column(
        horizontalAlignment=
            Alignment.CenterHorizontally
    ){

        Box(
            modifier=
                Modifier
                    .width(18.dp)
                    .height(value.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart=10.dp,
                            topEnd=10.dp
                        )
                    )
                    .background(
                        Color(0xFF8B3DFF)
                    )
        )

        Spacer(
            Modifier.height(8.dp)
        )

        Text(
            day,
            color=Color.White
        )

    }

}

@Composable
fun PlanCard(
    title:String,
    time:String
){

    Card(
        modifier=
            Modifier.fillMaxWidth(),

        colors=
            CardDefaults.cardColors(
                containerColor=
                    Color(0xFF1A2033)
            )
    ){

        Row(
            modifier=
                Modifier
                    .fillMaxWidth()
                    .padding(18.dp),

            horizontalArrangement=
                Arrangement.SpaceBetween
        ){

            Column{

                Text(
                    title,
                    color=Color.White
                )

                Text(
                    "Smart Study Daily Goal",

                    color=
                        Color(0xFF8B3DFF)
                )

            }

            Text(
                time,
                color=Color.White
            )

        }

    }

}
@Composable
fun SmallStatCard(
    title: String,
    value: String
) {

    Card(

        modifier =
            Modifier.width(160.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color(0xFF1B2235)
            )

    ) {

        Column(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally

        ) {

            Text(
                text = title,
                color = Color.Gray
            )

            Spacer(
                Modifier.height(6.dp)
            )

            Text(
                text = value,
                color = Color.White,
                style =
                    MaterialTheme.typography.headlineSmall
            )

        }

    }

}