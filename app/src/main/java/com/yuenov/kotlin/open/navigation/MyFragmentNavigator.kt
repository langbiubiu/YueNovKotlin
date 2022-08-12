package com.yuenov.kotlin.open.navigation

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator
import com.yuenov.kotlin.open.constant.PreferenceConstants.EXTRA_INT_BOOK_ID
import com.yuenov.kotlin.open.fragment.DetailFragment
import java.util.ArrayDeque

/**
 * 参考[me.hgj.jetpackmvvm.navigation.FragmentNavigatorHideShow]
 * 使用Hide/Show处理Fragment，使Fragment执行 onPause/onResume.避免页面重建.
 * 另外满足需要多个DetailFragment实例，
 */
@Navigator.Name("fragment")
class MyFragmentNavigator(
    private val mContext: Context,
    private val mFragmentManager: FragmentManager,
    private val mContainerId: Int
) : FragmentNavigator(mContext, mFragmentManager, mContainerId) {

    override fun navigate(
        destination: Destination,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras?
    ): NavDestination? {
        if (mFragmentManager.isStateSaved) {
            Log.i(
                TAG, "Ignoring navigate() call: FragmentManager has already"
                        + " saved its state"
            )
            return null
        }
        var className = destination.className
        if (className[0] == '.') {
            className = mContext.packageName + className
        }
        //final Fragment frag = instantiateFragment(mContext, mManager,
        //       className, args);
        //frag.setArguments(args);
        val ft = mFragmentManager.beginTransaction()
        var enterAnim = navOptions?.enterAnim ?: -1
        var exitAnim = navOptions?.exitAnim ?: -1
        var popEnterAnim = navOptions?.popEnterAnim ?: -1
        var popExitAnim = navOptions?.popExitAnim ?: -1
        if (enterAnim != -1 || exitAnim != -1 || popEnterAnim != -1 || popExitAnim != -1) {
            enterAnim = if (enterAnim != -1) enterAnim else 0
            exitAnim = if (exitAnim != -1) exitAnim else 0
            popEnterAnim = if (popEnterAnim != -1) popEnterAnim else 0
            popExitAnim = if (popExitAnim != -1) popExitAnim else 0
            ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
        }
        // 隐藏正在显示的Fragment
        val fragment = mFragmentManager.primaryNavigationFragment
        if (fragment != null) {
            ft.setMaxLifecycle(fragment, Lifecycle.State.STARTED)
            ft.hide(fragment)
        }
        // 通过tag获取到目标Fragment，然后show
        // 如果为空就生成一个实例，并add到FragmentStore中
        // 不使用replace方法，是为了避免Fragment重建
        val tag =
            if (className == DetailFragment::class.java.name)
                "${destination.id}_${args?.get(EXTRA_INT_BOOK_ID)}"
            else destination.id.toString()
//        val tag = destination.id.toString()
        var frag = mFragmentManager.findFragmentByTag(tag)
        if (frag != null) {
            ft.setMaxLifecycle(frag, Lifecycle.State.RESUMED)
            ft.show(frag)
        } else {
            frag = mFragmentManager.fragmentFactory.instantiate(mContext.classLoader, className)
            frag.arguments = args
            ft.add(mContainerId, frag, tag)
        }
        //ft.replace(mContainerId, frag);
        ft.setPrimaryNavigationFragment(frag)
        @IdRes val destId = destination.id
        var mBackStack: ArrayDeque<Int>? = null
        try {
            val field = FragmentNavigator::class.java.getDeclaredField("mBackStack")
            field.isAccessible = true
            mBackStack = field[this] as ArrayDeque<Int>
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        val initialNavigation = mBackStack!!.isEmpty()
        // Build first class singleTop behavior for fragments
        val isSingleTopReplacement = (navOptions != null && !initialNavigation
                && navOptions.shouldLaunchSingleTop()
                && mBackStack.peekLast() == destId)
        val isAdded: Boolean = if (initialNavigation) {
            true
        } else if (isSingleTopReplacement) {
            // Single Top means we only want one instance on the back stack
            if (mBackStack.size > 1) {
                // If the Fragment to be replaced is on the FragmentManager's
                // back stack, a simple replace() isn't enough so we
                // remove it from the back stack and put our replacement
                // on the back stack in its place
                mFragmentManager.popBackStack(
                    generateBackStackName(mBackStack.size, mBackStack.peekLast()),
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                ft.addToBackStack(generateBackStackName(mBackStack.size, destId))
            }
            false
        } else {
            ft.addToBackStack(generateBackStackName(mBackStack.size + 1, destId))
            true
        }
        if (navigatorExtras is Extras) {
            for ((key, value) in navigatorExtras.sharedElements) {
                ft.addSharedElement(key!!, value!!)
            }
        }
        ft.setReorderingAllowed(true)
        ft.commit()
        // The commit succeeded, update our view of the world
        return if (isAdded) {
            mBackStack.add(destId)
            destination
        } else {
            null
        }
    }

    private fun generateBackStackName(backStackIndex: Int, destId: Int?): String {
        return "$backStackIndex-$destId"
    }

    companion object {
        private const val TAG = "MyFragmentNavigator"
    }
}