package response

/**
 * Created by Ionut Andonescu <ionut.andonescu@pure360.com>
 */
case class ResponseForm(success: Boolean = false, message: String = "", errors: List[ResponseFormError] = List()) extends Serializable {
}

case class ResponseFormError(fieldName: String, message: String) extends Serializable{

}

