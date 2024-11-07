package com.example.pygmyhippo.common;

/*
Recycle view listener
Purpose:
    - To listen for when an item on the recycle view is clicked
Issues:
    - None
 */

/**
 * Interface for implementing a click listener for a recycler.
 * This is generally implemented in the parent fragment of the RecyclerView. You generally need a
 * reference to the list of data being displayed in the RecyclerView to effectively manipulate it
 * in onItemClick.
 *
 * @see com.example.pygmyhippo.admin.AllEventsFragment
 */
public interface RecyclerClickListener {
    void onItemClick(int position);
}
