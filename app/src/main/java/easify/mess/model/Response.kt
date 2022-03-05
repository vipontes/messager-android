package easify.mess.model

class Response {
    var success: Boolean = true
    var message: String

    constructor(message: String) {
        this.message = message
    }

    constructor(success: Boolean, message: String) {
        this.success = success
        this.message = message
    }
}