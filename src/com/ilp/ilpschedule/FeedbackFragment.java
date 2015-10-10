package com.ilp.ilpschedule;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;

import com.ilp.ilpschedule.model.Feedback;
import com.ilp.ilpschedule.util.Constants;
import com.tcs.myilp.R;

public class FeedbackFragment extends Fragment {
	public static final String TAG = "com.tcs.myilp.FeedbackFragment";

	private EditText editTextFaculty, editTextCourse, editTextComment;
	private ImageButton imageButtonFeedbackSend;
	private RatingBar ratingBarFeedback;
	private OnClickListener feedbackSubmitClickListner = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// send feedback to server
			Feedback feedback = new Feedback();
			feedback.setComment(editTextComment.getText().toString());
			feedback.setRating(ratingBarFeedback.getRating());
			feedback.setSlot_id(1);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_feedback, container,
				false);
		if (editTextCourse == null)
			editTextCourse = (EditText) rootView
					.findViewById(R.id.editTextFeedbackCourse);
		if (editTextFaculty == null)
			editTextFaculty = (EditText) rootView
					.findViewById(R.id.editTextFeedbackFaculty);
		if (imageButtonFeedbackSend == null) {
			imageButtonFeedbackSend = (ImageButton) rootView
					.findViewById(R.id.imageButtonFeedbackSend);
			imageButtonFeedbackSend
					.setOnClickListener(feedbackSubmitClickListner);
		}
		if (ratingBarFeedback == null) {
			ratingBarFeedback = (RatingBar) rootView
					.findViewById(R.id.ratingBarFeedback);
		}
		if (editTextComment == null) {
			editTextComment = (EditText) rootView
					.findViewById(R.id.editTextFeedbackComment);
		}
		return rootView;
	}

	@Override
	public void onStart() {
		Bundle bundle = getArguments();
		editTextFaculty.setText((String) bundle
				.get(Constants.BUNDLE_KEYS.FEEDBACK_FRAGMENT.FACULTY));
		editTextCourse.setText((String) bundle
				.get(Constants.BUNDLE_KEYS.FEEDBACK_FRAGMENT.COURSE));
		super.onStart();
	}

	@Override
	public void onResume() {
		getActivity().setTitle(R.string.title_feedback);
		super.onResume();
	}
}
