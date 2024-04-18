package com.lee_idle.livedatademo.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.lee_idle.livedatademo.R
import com.lee_idle.livedatademo.databinding.FragmentGameBinding

class GameFragment : Fragment() {
    private lateinit var binding: FragmentGameBinding

    // fragment가 처음 생성될 때 뷰모델을 생성
    // 조각이 다시 생성되면 첫 번째 조각에서 생성된 것과
    // 동일한 인스턴스를 받습니다.
    private val viewModel: GameViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)
        return binding.root
        //return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 데이터 바인딩을 위해 뷰모델을 설정하면 바인딩된
        // 레이아웃이 뷰모델의 모든 데이터에 액세스할 수 있습니다.
        binding.gameViewModel = viewModel
        binding.maxNoOfWords = MAX_NO_OF_WORDS
        // fragment view를 바인딩의 수명 주기 소유자로 지정합니다.
        // 이는 바인딩이 라이브 데이터 업데이트를 관찰할 수 있도록 하기 위해 사용됩니다.
        binding.lifecycleOwner = viewLifecycleOwner

        // 제출 및 스킵 버튼의 클릭 리스너 설정
        binding.btnSubmit.setOnClickListener{ onSubmitWord() }
        binding.btnSkip.setOnClickListener{ onSkipWord() }
        if(viewModel.isGameOver()){
            showFinalScoreDialog()
        }
    }

    /**
     * 사용자의 단어를 확인하고 그에 따라 점수를 업데이트합니다.
     * 다음 스크램블 단어를 표시합니다.
     * 마지막 단어 뒤에 최종 점수가 포함된 대화 상자를 표시합니다.
     */
    private fun onSubmitWord() {
        val playerWord = binding.textInputEditText.text.toString()

        if (viewModel.isUserWordCorrect(playerWord)) {
            setErrorTextField(false)
            if (!viewModel.nextWord()) {
                showFinalScoreDialog()
            }
        } else {
            setErrorTextField(true)
        }
    }

    /**
     * 점수를 변경하지 않고 현재 단어를 건너뜁니다.
     * 단어 수를 늘립니다.
     * 마지막 단어 뒤에 최종 점수가 표시된 대화 상자가 사용자에게 표시됩니다.
     */
    private fun onSkipWord() {
        if (viewModel.nextWord()) {
            setErrorTextField(false)
        } else {
            showFinalScoreDialog()
        }
    }

    /**
     * 뷰모델의 데이터를 다시 초기화하고
     * 뷰를 새 데이터로 업데이트하여 다음을 수행합니다.
     * 게임 재시작.
     */
    private fun restartGame() {
        viewModel.reinitializeData()
        setErrorTextField(false)
    }

    /**
     * 게임 종료
     *
     */
    private fun exitGame() {
        activity?.finish()
    }

    /**
     * 최종 점수가 포함된 알림 대화 상자를 생성하고 표시합니다.
     *
     */
    private fun showFinalScoreDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.congratulations))
            .setMessage(getString(R.string.you_scored, viewModel.score.value))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.exit)) { _, _ ->
                exitGame()
            }
            .setPositiveButton(getString(R.string.play_again)) { _, _ ->
                restartGame()
            }
            .show()
    }

    /**
     * 텍스트 필드 오류 상태를 설정하고 재설정합니다.
     * @param error: 오류 여부
     */
    private fun setErrorTextField(error: Boolean) {
        if (error) {
            binding.textField.isErrorEnabled = true
            binding.textField.error = getString(R.string.try_again)
        } else {
            binding.textField.isErrorEnabled = false
            binding.textInputEditText.text = null
        }
    }
}