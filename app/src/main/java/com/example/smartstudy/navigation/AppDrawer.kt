package com.example.smartstudy.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawer(
    onItemClick: (String) -> Unit
) {

    ModalDrawerSheet {

        DrawerItem(
            title = "Home",
            icon = Icons.Default.Home,

            onClick = {
                onItemClick("Home")
            }
        )

        DrawerItem(
            title = "Planner",
            icon = Icons.Default.DateRange,

            onClick = {
                onItemClick("Planner")
            }
        )

        DrawerItem(
            title = "Leaderboard",
            icon = Icons.Default.EmojiEvents,

            onClick = {
                onItemClick("Leaderboard")
            }
        )

        DrawerItem(
            title = "Assessment",
            icon = Icons.Default.Assignment,

            onClick = {
                onItemClick("Assessment")
            }
        )

        DrawerItem(
            title = "Practice",
            icon = Icons.Default.Code,

            onClick = {
                onItemClick("Practice")
            }
        )

        DrawerItem(
            title = "AI Assistant",
            icon = Icons.Default.SmartToy,

            onClick = {
                onItemClick("AI")
            }
        )

        DrawerItem(
            title = "Profile",
            icon = Icons.Default.Person,

            onClick = {
                onItemClick("Profile")
            }
        )

        DrawerItem(
            title = "Settings",
            icon = Icons.Default.Settings,

            onClick = {
                onItemClick("Settings")
            }
        )

        DrawerItem(
            title = "Premium",
            icon = Icons.Default.Star,

            onClick = {
                onItemClick("Premium")
            }
        )

        DrawerItem(
            title = "Logout",
            icon = Icons.Default.ExitToApp,

            onClick = {
                onItemClick("Logout")
            }
        )

    }

}

@Composable
fun DrawerItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {

    NavigationDrawerItem(

        label = {
            Text(text = title)
        },

        selected = false,

        onClick = {
            onClick()
        },

        icon = {

            Icon(
                imageVector = icon,
                contentDescription = null
            )

        },

        modifier =
            Modifier.padding(
                horizontal = 12.dp,
                vertical = 4.dp
            )

    )

}
