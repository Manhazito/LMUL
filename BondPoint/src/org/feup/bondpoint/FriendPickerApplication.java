package org.feup.bondpoint;

import java.util.Collection;

import android.app.Application;

import com.facebook.model.GraphUser;

// We use a custom Application class to store our minimal state data (which users have been selected).
// A real-world application will likely require a more robust data model.
public class FriendPickerApplication extends Application {
	private Collection<GraphUser> selectedUsers;

	public Collection<GraphUser> getSelectedUsers() {
		return selectedUsers;
	}

	public void setSelectedUsers(Collection<GraphUser> selectedUsers) {
		this.selectedUsers = selectedUsers;
	}
}
