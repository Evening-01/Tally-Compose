package com.evening.tally.ui.pages.common

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.evening.tally.data.preference.LocalDarkTheme
import com.evening.tally.ext.isFirstLaunch
import com.evening.tally.ext.string
import com.evening.tally.ui.component.animatedComposable
import com.evening.tally.ui.component.bottomNavScreenList
import com.evening.tally.ui.pages.other.StartPage
import com.evening.tally.ui.pages.screens.DataScreen
import com.evening.tally.ui.pages.screens.SettingScreen
import com.evening.tally.ui.theme.AppTheme
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController


@OptIn(ExperimentalAnimationApi::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun HomeEntry() {
    val navController = rememberAnimatedNavController()
    val context = LocalContext.current

    //将底部栏菜单数据的路由名整成一个list
    val routes = bottomNavScreenList.map { it.route }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val useDarkTheme = LocalDarkTheme.current.isDarkTheme()

    AppTheme(useDarkTheme = useDarkTheme) {
        rememberSystemUiController().run {
            setStatusBarColor(Color.Transparent, !useDarkTheme)
            setSystemBarsColor(Color.Transparent, !useDarkTheme)
            setNavigationBarColor(Color.Transparent, !useDarkTheme)
        }
        Scaffold(bottomBar = {
            //只要当页面为首页,才展示底部菜单栏
            if (currentDestination?.hierarchy?.any { routes.contains(it.route) } == true) {
                BottomNavigationBar(navController = navController)
            }
//            BottomNavigationBar(navController = navController)
        }, content = { padding ->
            NavHostContainer(navController = navController, padding = padding)
        })
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {

    NavigationBar() {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        bottomNavScreenList.forEach { navItem ->
            NavigationBarItem(selected = currentRoute == navItem.route, onClick = {
                navController.navigate(navItem.route) {
                    //使用此方法,可以避免生成一个重复的路由堆栈
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    //避免重复选择会创建一个新的页面副本
                    launchSingleTop = true
                    //当重新选择之前已选择项目恢复页面状态
                    restoreState = true
                }
            }, icon = {
                Icon(imageVector = navItem.imageId, contentDescription = navItem.stringId.string)
            }, label = {
                Text(text = navItem.stringId.string)
            })
        }
    }
}




@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavHostContainer(
    navController: NavHostController, padding: PaddingValues
) {
    val context = LocalContext.current
    AnimatedNavHost(
        navController,
        startDestination = if (context.isFirstLaunch) Route.START else Route.DATA,
        Modifier
            .fillMaxWidth()
            .padding(bottom = padding.calculateBottomPadding())
    ) {
        animatedComposable(Route.START) {
            StartPage(navController)
        }
        composable(Route.DATA) {
            DataScreen()
        }

        composable(Route.SETTING) {
            SettingScreen(navController = navController)
        }
    }
}