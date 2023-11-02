import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.poseexercise.FirstOnboardingFragment
import com.example.poseexercise.SecondOnboardingFragment
import com.example.poseexercise.ThirdOnboardingFragment

class OnboardingPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private val fragments = listOf(
        FirstOnboardingFragment(),
        SecondOnboardingFragment(),
        ThirdOnboardingFragment()
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}
