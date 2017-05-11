package it.polito.group05.group05;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import it.polito.group05.group05.Utility.BaseClasses.Singleton;
import it.polito.group05.group05.Utility.BaseClasses.UserDatabase;
import it.polito.group05.group05.Utility.Holder.PersonHolder;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GroupDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GroupDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupDetailsFragment extends Fragment {

    RecyclerView rv;
    Context context;

    private OnFragmentInteractionListener mListener;

    public GroupDetailsFragment() {
        // Required empty public constructor

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GroupDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupDetailsFragment newInstance() {
        GroupDetailsFragment fragment = new GroupDetailsFragment();
        //  Bundle args = new Bundle();
        //  args.putString(ARG_PARAM1, param1);
        //  args.putString(ARG_PARAM2, param2);
        //  fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
  /*      if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_group_details, container, false);
        rv = (RecyclerView) rootView.findViewById(R.id.detail_fragment_rv);
        rv.setHasFixedSize(false);
        final LinearLayoutManager ll=  new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,true);
        rv.setLayoutManager(ll);
        ll.setStackFromEnd(true);
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference usersGroupRef = FirebaseDatabase.getInstance().getReference("groups").child(Singleton.getInstance().getIdCurrentGroup()).child("members");
        FirebaseIndexRecyclerAdapter personAdapter = new FirebaseIndexRecyclerAdapter(UserDatabase.class,
                R.layout.item_person_details_frag,
                PersonHolder.class,
                usersGroupRef,
                usersRef){
            @Override
            protected void populateViewHolder(RecyclerView.ViewHolder viewHolder, Object model, int position) {
                ((PersonHolder)viewHolder).setData(model,getContext());
            }

            @Override
            protected Object parseSnapshot(DataSnapshot snapshot) {
                //if(snapshot.getKey().equals(Singleton.getInstance().getCurrentUser().getId())) return null;
                for (DataSnapshot child : snapshot.getChildren()) {
                    if (child.getKey().equals("userInfo"))
                        return child.getValue(UserDatabase.class);
                }
                return null;
                //return super.parseSnapshot(sp);
            }


        };
        rv.setAdapter(personAdapter);
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
