package de.jvstvshd.localstream.client.desktop.util.activity

import java.util.*

class NetworkActivity(state: State, type: ActivityType, description: String, uuid: UUID) {

    var activityState: State
    var activityType: ActivityType
    var activiyDescription: String
    var id: UUID
    var progress: Double

    init {
        activityState = state
        activityType = type
        activiyDescription = description
        id = uuid
        progress = 0.0
    }

    enum class State {
        NOT_INITIALIZED,
        INITIALIZED,
        STARTED,
        COMPUTED_SUCCESS,
        COMPUTED_FAIL;
    }

    enum class ActivityType {
        ADD_TITLE,
        UPLOAD_FILE,
        UPLOAD_TITLE,
        EDIT_TITLE,
        DELETE_TITLE,
        UNKNOWN,
        SEARCH;
    }
}