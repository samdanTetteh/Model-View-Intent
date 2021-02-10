/*
 * Copyright (c) 2019 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.android.creaturemon.addcreature

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.raywenderlich.android.creaturemon.R
import com.raywenderlich.android.creaturemon.data.model.AttributeStore
import com.raywenderlich.android.creaturemon.data.model.AttributeValue
import com.raywenderlich.android.creaturemon.data.model.Avatar
import com.raywenderlich.android.creaturemon.addcreature.avatars.AvatarAdapter
import com.raywenderlich.android.creaturemon.addcreature.avatars.AvatarBottomDialogFragment
import com.raywenderlich.android.creaturemon.mviBase.MviView
import com.raywenderlich.android.creaturemon.util.CreaturemonViewModelFactory
import com.raywenderlich.android.creaturemon.util.visible
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_creature.*


class CreatureActivity : AppCompatActivity(), AvatarAdapter.AvatarListener, MviView<AddCreatureIntents, AddCreatureViewState> {

    private val avatarIntentPublisher =
            PublishSubject.create<AddCreatureIntents.AvatarIntent>()

    private val nameIntentPublisher =
            PublishSubject.create<AddCreatureIntents.NameIntent>()

    private val intelligencePublisher =
            PublishSubject.create<AddCreatureIntents.IntelligenceIntent>()

    private val strengthPublisher =
            PublishSubject.create<AddCreatureIntents.StrengthIntent>()

    private val endurancePublisher =
            PublishSubject.create<AddCreatureIntents.EnduranceIntent>()


    private val savePublisher =
            PublishSubject.create<AddCreatureIntents.SaveIntent>()

    private val disposables = CompositeDisposable()

    private var avatarResourceId = 0 //TODO: save in ViewModel

    private val viewModel: AddCreatureViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(this, CreaturemonViewModelFactory.getInstance(this))
                .get(AddCreatureViewModel::class.java)
    }


    override fun onStart() {
        super.onStart()
        bind()
    }

    private fun bind() {
        disposables.add(viewModel.states().subscribe(this::render))
        viewModel.processIntents(intents())
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creature)

        configureUI()
        configureSpinnerAdapters()
        configureSpinnerListeners()
        configureEditText()
        configureClickListeners()
    }

    override fun intents(): Observable<AddCreatureIntents> {
        return Observable.merge(
                avatarIntent(),
                nameIntent(),
                intelligenceIntent(),
                strengthIntent())
                .mergeWith(enduranceIntent())
                .mergeWith(saveIntent())
    }

    override fun render(state: AddCreatureViewState) {
        if (state.isSaveComplete) {
            Toast.makeText(this, getString(R.string.creature_saved), Toast.LENGTH_LONG).show()
            finish()
            return
        }

        if (state.isProcessing) {
            progressBar.visible = true
            saveButton.visible = false
        } else {
            progressBar.visible = false
            saveButton.visible = true
        }

        if (state.isDrawableSelected) {
            avatarImageView.setImageResource(state.creature.drawable)
            avatarResourceId = state.creature.drawable
            hideTapLabel()
        }

        hitPoints.text = state.creature.hitPoints.toString()

        if (state.error != null) {
            Toast.makeText(this, state.error.message, Toast.LENGTH_LONG).show()
          Log.e(TAG, "Error creating creature: ${state.error.localizedMessage}")
        }


    }

    private fun avatarIntent(): Observable<AddCreatureIntents.AvatarIntent> {
        return avatarIntentPublisher
    }

    private fun nameIntent(): Observable<AddCreatureIntents.NameIntent> {
        return nameIntentPublisher
    }

    private fun intelligenceIntent(): Observable<AddCreatureIntents.IntelligenceIntent> {
        return intelligencePublisher
    }

    private fun strengthIntent(): Observable<AddCreatureIntents.StrengthIntent> {
        return strengthPublisher
    }

    private fun enduranceIntent(): Observable<AddCreatureIntents.EnduranceIntent> {
        return endurancePublisher
    }

    private fun saveIntent(): Observable<AddCreatureIntents.SaveIntent> {
        return savePublisher
    }

    private fun configureUI() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.add_creature)
    }

    private fun configureSpinnerAdapters() {
        intelligence.adapter = ArrayAdapter<AttributeValue>(this,
                android.R.layout.simple_spinner_dropdown_item, AttributeStore.INTELLIGENCE)
        strength.adapter = ArrayAdapter<AttributeValue>(this,
                android.R.layout.simple_spinner_dropdown_item, AttributeStore.STRENGTH)
        endurance.adapter = ArrayAdapter<AttributeValue>(this,
                android.R.layout.simple_spinner_dropdown_item, AttributeStore.ENDURANCE)
    }

    private fun configureSpinnerListeners() {
        intelligence.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                intelligencePublisher.onNext(AddCreatureIntents.IntelligenceIntent(position))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        strength.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                strengthPublisher.onNext(AddCreatureIntents.StrengthIntent(position))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        endurance.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                endurancePublisher.onNext(AddCreatureIntents.EnduranceIntent(position))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun configureEditText() {
        nameEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                nameIntentPublisher.onNext(AddCreatureIntents.NameIntent(s.toString()))
            }
        })
    }

    private fun configureClickListeners() {
        avatarImageView.setOnClickListener {
            val bottomDialogFragment = AvatarBottomDialogFragment.newInstance()
            bottomDialogFragment.show(supportFragmentManager, "AvatarBottomDialogFragment")
        }

        saveButton.setOnClickListener {
            savePublisher.onNext(AddCreatureIntents.SaveIntent(
                    avatarResourceId,
                    nameEditText.text.toString(),
                    intelligence.selectedItemPosition,
                    strength.selectedItemPosition,
                    endurance.selectedItemPosition
            ))
        }
    }

    override fun avatarClicked(avatar: Avatar) {
        avatarIntentPublisher.onNext(AddCreatureIntents.AvatarIntent(
                avatar.drawable
        ))
        hideTapLabel()
    }

    private fun hideTapLabel() {
        tapLabel.visibility = View.INVISIBLE
    }

    companion object {
        private const val TAG = "CREATURE-ACTIVITY"
    }
}
