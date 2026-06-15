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
import androidx.compose.ui.graphics.luminance
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

    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val backgroundColor = if (isDark) Color(0xFF050B1A) else Color(0xFFF3F4F6)
    val backgroundEndColor = if (isDark) Color(0xFF0A1B55) else Color(0xFFE5E7EB)
    val textColorMain = if (isDark) Color.White else Color(0xFF1F2937)
    val textColorMuted = if (isDark) Color.LightGray else Color(0xFF4B5563)
    val textColorSecondary = if (isDark) Color.Gray else Color(0xFF6B7280)
    val cardBgColor = if (isDark) Color(0xFF1B2235) else Color(0xFFFFFFFF)
    val cardBgColorSecondary = if (isDark) Color(0xFF1A2033) else Color(0xFFFFFFFF)

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
                                backgroundColor,
                                backgroundEndColor
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
                                text = "Welcome Back 👋",
                                color = textColorSecondary,
                                fontSize = 16.sp
                            )

                            Spacer(
                                Modifier.height(4.dp)
                            )

                            Text(
                                text = "Vishwa",
                                color = textColorMain,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(
                                Modifier.height(4.dp)
                            )

                            Text(
                                text = "Let's continue your learning journey 🚀",
                                color = textColorMuted,
                                fontSize = 14.sp
                            )

                        }

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
                                fontSize = 38.sp,
                                fontWeight = FontWeight.ExtraBold
                            )

                            Text(
                                "Keep your streak alive!",
                                color = Color.White.copy(alpha = 0.8f)
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
                        containerColor = cardBgColor
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF8B3DFF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "V",
                                color = Color.White,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {

                            Text(
                                text = "Vishwa",
                                color = textColorMain,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "B.Tech - CSE",
                                color = textColorMuted
                            )

                            Text(
                                text = "SIMATS University",
                                color = textColorSecondary
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Keep Learning 🚀",
                                color = Color(0xFF8B5CF6)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Courses",
                        value = "4"
                    )

                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Tests",
                        value = "2"
                    )

                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = "Avg Score",
                        value = "85%"
                    )
                }
                Spacer(
                    Modifier.height(25.dp)
                )
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = cardBgColor
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {

                        Text(
                            text = "📚 Course Progress",
                            color = textColorMain,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        LinearProgressIndicator(
                            progress = { 0.72f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = Color(0xFF8B3DFF)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "72% Completed",
                            color = Color(0xFF8B3DFF),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    "Quick Access",
                    color = textColorMain,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(15.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween
                ) {

                    QuickCard("Courses","📚")
                    QuickCard("AI","✅")
                    QuickCard("Progress","🎯")
                    QuickCard("Rank","📈")

                }

                Spacer(Modifier.height(30.dp))

                SectionCard(
                    "AI Prediction Score",
                    "91% probability of scoring above A grade"
                )

                Spacer(Modifier.height(25.dp))

                SectionCard(
                    "Continue Learning",
                    "DSA + Java Interview Prep - 72%"
                )

                Spacer(Modifier.height(25.dp))

                WeeklyGraph()

                Spacer(Modifier.height(25.dp))

                Text(
                    "Today's Plan",
                    color=textColorMain,
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
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val textColorMain = if (isDark) Color.White else Color(0xFF1F2937)
    val cardBgColorSecondary = if (isDark) Color(0xFF1A2033) else Color(0xFFFFFFFF)

    Card(
        modifier=
            Modifier
                .width(90.dp)
                .height(100.dp)
                .clickable {},

        colors=
            CardDefaults.cardColors(
                containerColor=cardBgColorSecondary
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
                fontSize=30.sp
            )

            Spacer(
                Modifier.height(6.dp)
            )

            Text(
                title,
                color=textColorMain
            )

        }

    }

}

@Composable
fun SectionCard(
    title:String,
    subtitle:String
){
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val textColorMain = if (isDark) Color.White else Color(0xFF1F2937)
    val cardBgColorSecondary = if (isDark) Color(0xFF1A2033) else Color(0xFFFFFFFF)

    Card(
        colors=
            CardDefaults.cardColors(
                containerColor=cardBgColorSecondary
            )
    ){

        Column(
            modifier=
                Modifier.padding(18.dp)
        ){

            Text(
                title,
                color=textColorMain,
                fontSize=13.sp
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
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val textColorMain = if (isDark) Color.White else Color(0xFF1F2937)
    val cardBgColorSecondary = if (isDark) Color(0xFF1A2033) else Color(0xFFFFFFFF)

    Card(
        colors=
            CardDefaults.cardColors(
                containerColor=cardBgColorSecondary
            )
    ){

        Column(
            modifier=
                Modifier.padding(18.dp)
        ){

            Text(
                "Weekly Student Performance 📈",
                color=textColorMain,
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
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val textColorMain = if (isDark) Color.White else Color(0xFF1F2937)

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
            color=textColorMain
        )

    }

}

@Composable
fun PlanCard(
    title:String,
    time:String
){
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val textColorMain = if (isDark) Color.White else Color(0xFF1F2937)
    val cardBgColorSecondary = if (isDark) Color(0xFF1A2033) else Color(0xFFFFFFFF)

    Card(
        modifier=
            Modifier.fillMaxWidth(),

        colors=
            CardDefaults.cardColors(
                containerColor=cardBgColorSecondary
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
                    color=textColorMain
                )

                Text(
                    "Smart Study Daily Goal",

                    color=
                        Color(0xFF8B3DFF)
                )

            }

            Text(
                time,
                color=textColorMain
            )

        }

    }

}
@Composable
fun SmallStatCard(
    title: String,
    value: String
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val textColorMain = if (isDark) Color.White else Color(0xFF1F2937)
    val textColorSecondary = if (isDark) Color.Gray else Color(0xFF6B7280)
    val cardBgColor = if (isDark) Color(0xFF1B2235) else Color(0xFFFFFFFF)

    Card(

        modifier =
            Modifier.width(160.dp),

        colors =
            CardDefaults.cardColors(
                containerColor = cardBgColor
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
                color = textColorSecondary
            )

            Spacer(
                Modifier.height(6.dp)
            )

            Text(
                text = value,
                color = textColorMain,
                style =
                    MaterialTheme.typography.headlineSmall
            )

        }

    }

}
@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val textColorMain = if (isDark) Color.White else Color(0xFF1F2937)
    val textColorSecondary = if (isDark) Color.Gray else Color(0xFF6B7280)
    val cardBgColor = if (isDark) Color(0xFF1B2235) else Color(0xFFFFFFFF)

    Card(
        modifier = modifier.height(90.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardBgColor
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = value,
                color = textColorMain,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                color = textColorSecondary,
                fontSize = 12.sp
            )
        }
    }
}