// @file:OptIn(ExperimentalUnsignedTypes::class)
// For now, we won't use unsigned types, since they're
// not yet part of the Kotlin specification.
//@ExperimentalUnsignedTypes
package stegandcryptography

import java.awt.image.BufferedImage
import javax.imageio.ImageIO

import java.awt.Color

import java.io.File
import java.io.IOException
//import java.io.IOException
// import java.io.FileNotFoundException
import javax.imageio.IIOException
import kotlin.experimental.xor

const val HIDE_COMMAND = "hide"
const val SHOW_COMMAND = "show"
const val EXIT_COMMAND = "exit"
// const val HELP_COMMAND = "help"
// const val EMPTY_LINE = ""
const val BYE = "Bye!"
const val RESULT_SUCCESS = "Success"
const val RESULT_FAILURE = "Failure"
const val INPUT_IMAGE_FILE_PROMPT = "Input image file:"
const val OUTPUT_IMAGE_FILE_PROMPT = "Output image file:"
const val HIDDEN_MESSAGE_PROMPT = "Message to hide:"
const val PASSWORD_PROMPT = "Password:"
// I haven't figured out yet how to determine if the message
// is too long to be inserted into the blue parts of the
// output file's BufferedImage.
const val MESSAGE_TOO_LARGE = "The input image is not large enough to hold this message."
const val MESSAGE_SAVED_IN = "Message saved in"
const val MESSAGE_SAVED_IN_2 = "image"
// const val INPUT_IMAGE_FILE_SHOW = "Input image file:"
// const val INPUT_FILE_DOES_NOT_EXIST = "The input file does not exist."
const val INPUT_FILE_DOES_NOT_EXIST = "Can't read input file!"
const val INPUT_FILE_READ_SUCCESSFULLY = "The input file was read successfully"
const val INPUT_AND_OUTPUT_FILE_NAMES_NOW_AVAILABLE = "The input and output file names are now available."
const val OUTPUT_FILE_WRITE_SUCCEEDED = "The output file write succeeded."
// const val OUTPUT_FILE_IS_SAVED_FRAG_1 = "Image"
// const val OUTPUT_FILE_IS_SAVED_FRAG_2 = "image"
// const val OUTPUT_FILE_WRITE_FAILED = "The output file write failed."
const val MSG_NOT_FOUND = "Hidden message not found!"
// const val MSG_EXIT = "Bye!"
// const val MSG_WRONG_CMD = "Wrong task:"
// const val MSG_INPUT_FILENAME = "Input image file:"
// const val OUTPUT_FILENAME_PROMPT = "Output image file:"
const val MSG_IMAGE_TOO_SMALL = "The input image is not large enough to hold this message."
//const val MSG_TO_HIDE = "Message to hide:"
const val MSG_SHOW = "Message:"
const val MSG_INPUT_FILENAME = "Input image file:"
const val MSG_PWD = "Password:"
// 3-byte (24-bit) string representation of bytes 0,0,3 that indicates the
// end of the secret message
const val END_OF_MESSAGE = "000000000000000000000011"
const val IMG_FILE_EXTENSION = "png"
// const val TASK_PROMPT = "Task (hide, show, exit, blues):"
const val TASK_PROMPT = "Task (hide, show, exit):"
//const val ZERO = 0
const val TWO = 2
//const val ONE = 1
//const val TWENTY_TWO = 22

//const val BYTE_ZERO: Byte = 0
//const val BYTE_THREE: Byte = 3
//const val IMG_TYPE = "png"

// const val PRINT_BLUES = "blues"

data class ResultData(
    var resultString: String,
    var resultText: String,
)

data class DataForHide(
    var resultString: String = "",
    var resultText: String = "",
    // var fileNames: Pair<String, String> = Pair("", ""),
    var inputFileName: String = "",
    var outputFileName: String = "",
    var messageToHide: String = "",
    var password: String = "",
    var baPassword: ByteArray = byteArrayOf(),
    var bufferedImage: BufferedImage? = null,
    // Result of encode toByteArray() and appending
    // 3 end-of-message marker bytes (0, 0, 3):
    var baMessage: ByteArray = byteArrayOf(),
    var baEndOfMessage: ByteArray = byteArrayOf(),
    var baEncryptedMessage: ByteArray = byteArrayOf(),
    // val inputFile: File // = File(getInputFromUser(MSG_INPUT_FILENAME))
    // val outputFile: File // = File(getInputFromUser(MSG_OUTPUT_FILENAME))

    var encryptedMessageAsIntList: List<Int> = emptyList(),

    var messageBits: List<Int> = emptyList(), // = getBitsAsIntList(getInputFromUser(MSG_TO_HIDE))
    var passwordBits: List<Int> = emptyList() // = getBitsAsIntList(getInputFromUser(PASSWORD_PROMPT))
) // end data class DataForHide

data class DataForShow(
    var resultString: String = "",
    var resultText: String = "",
    var inputFileName: String = "",
    var password: String = "",
    var baPassword: ByteArray = byteArrayOf(),
    var bufferedImage: BufferedImage? = null,
    // Result of encode toByteArray() and appending
    // 3 end-of-message marker bytes (0, 0, 3)
    var baMessage: ByteArray = byteArrayOf(),
    var baEndOfMessage: ByteArray = byteArrayOf(),
    var messageBits: List<Int> = emptyList(), // = getBitsAsIntList(getInputFromUser(MSG_TO_HIDE))
    var passwordBits: List<Int> = emptyList() // = getBitsAsIntList(getInputFromUser(PASSWORD_PROMPT))
) // end data class DataForShow

fun main() {
//    tryIt()
//    return


//    val ba = byteArrayOf(0x1, 0x1, 0x1)
//
//    val newba = addSecretMsgEndMarkers(ba) // [1, 1, 1, 0, 0, 3]
//    return

    do {
        /*
        var inputFile: String
        var outputFile: String
        val bufferedImage: BufferedImage
        */
        val task = getInputFromUser(TASK_PROMPT)
        when (task) {
            HIDE_COMMAND -> if (!processHide()) continue
                // println("Hiding secret message in image file.")

            SHOW_COMMAND -> if (!processShow()) continue
                // println("Obtaining message from image.")

            EXIT_COMMAND -> {
                break
            }
            // PRINT_BLUES -> printBlues(getInputFromUser(INPUT_IMAGE_FILE_PROMPT))
            else -> {
                println("Invalid task: $task")
                //continue
            }
        }
    } while (task != EXIT_COMMAND) // end do...
    processExit() // task is "exit"
// println(BYE)
// return
} // end main()

fun processExit() {
    println(BYE)
}

/**
* Displays a message containing the secret message that's
* hidden in the input file as a response to the user's
* requesting the "show" command.
*
* More specifically, something like
* this:
* Input image file:
* > hide.png
* Message:
* Hello World!
*
* @return a Boolean representing success (true) or failure (false)
*/
fun processShow(): Boolean {
//    cmdShow()
//    return true

    val dfs = getDataForShow()
    val inImgFile = File(dfs.inputFileName)
//    val passwordStr = getInputFromUser(PASSWORD_PROMPT)

    val inputImage: BufferedImage
    try {
        inputImage = ImageIO.read(inImgFile)
    } catch (e: IOException) {
        println("${e.message}")
        return false
    }

    //val passwordStr = getInputFromUser(PASSWORD_PROMPT)
//    val baPassword = passwordStr.encodeToByteArray()
//    println("${MSG_SHOW}\n${getHiddenMessageFromImage(inputImage, baPassword)}")
//  fun getHiddenMessageFromImage(dfs: DataForShow, bufferedImage: BufferedImage, password: String): String {
    println("${MSG_SHOW}\n${getHiddenMessageFromImage2(dfs, inputImage)}")

    return true
} // end processShow()

/*
 * > show
 * Input image file:
 * > enc.png
 * Password:
 * > mypassword
 * Message:
 * My encrypted message!
 */
fun getDataForShow(): DataForShow {
    val dfs = DataForShow()
    // Get the name of the input file and a File object based on it.
    dfs.inputFileName = getInputFromUser(INPUT_IMAGE_FILE_PROMPT)
    dfs.password = getInputFromUser(PASSWORD_PROMPT)
    dfs.baPassword = dfs.password.encodeToByteArray()
    // dfs.passwordBits = encodeToBits(dfs.password)
    dfs.passwordBits = getBitsAsIntList(dfs.password)

    try {
        dfs.bufferedImage = ImageIO.read(File(dfs.inputFileName))
    } catch (e: IOException) {
        dfs.resultString = RESULT_FAILURE
        dfs.resultText = "${e.message}"
        return dfs
    }

    val eomArray = byteArrayOf(0x0, 0x0, 0x3)
    dfs.baEndOfMessage = eomArray

    return dfs
} // end getDataForShow()

/*
 * Process a request for the "hide" command.
 * Hides the message inside the copy of an existing image file.
 * Note that the DataForHide ("dfh" below) includes a ByteArray
 * containing the encrypted message followed by the 3-byte
 * end-of-message markers.
 */
fun processHide(): Boolean {
    val dfh = getDataForHide()

    if (dfh.resultString == RESULT_FAILURE)
        return false

    val inImgFile = File(dfh.inputFileName)
    // val outImgFile = File(dfh.outputFileName)

    // 2023-02-21 14:51
    // This call seems ok. It calls
    // dfh.encryptedMessageAsIntList
    saveMessageToHideInOutputFile(dfh)

    return true
} // end processHide()

// I need to encrypt the secret message before I insert its bits
// into the output file!!! dfh.encrypted
fun getDataForHide(): DataForHide {
    val dfh = DataForHide()
    // Get the name of the input file and a File object based on it.
    val inputFileName = getInputFromUser(INPUT_IMAGE_FILE_PROMPT)
    val inputFile = File(inputFileName)
    dfh.inputFileName = inputFileName
    // Get the name of the output file and a File object based on it.
    val outputFileName = getInputFromUser(OUTPUT_IMAGE_FILE_PROMPT)
    // val outputFile = File(outputFileName)
    dfh.outputFileName = outputFileName
    // Get the message to hide and a list of integers that represents it.
    val messageToHide = getInputFromUser(HIDDEN_MESSAGE_PROMPT)
    // Add code to append the marker integers to messageToHide.
    var baMessage = messageToHide.encodeToByteArray()
    dfh.baMessage = baMessage // A ByteArray, NOT including the
    // 3 end-of-msg marker bytes
    //addSecretMsgEndMarkers(mb)
    //addSecretMsgEndMarkers(baMessage)

    // Should we encrypt the message first (before doing
    // the next statement). No, let's do that in processHide().
    val messageBits = getBitsAsIntList(messageToHide)

    val mb = messageBits.toMutableList()
    // Add 3 bytes of bits to mark the end of the secret message.
    //
    val password = getInputFromUser(PASSWORD_PROMPT)
    dfh.password = password
    val baPassword = password.encodeToByteArray()
    dfh.baPassword = baPassword
    dfh.passwordBits = getBitsAsIntList(password)

    //mb.addAll(arrayListOf(0, 0, 3))
    dfh.messageToHide = messageToHide // a String
    dfh.messageBits = mb // a MutableList<Int>
    // We need to encrypt the message using the password.
    var baEncryptedMessage = encryptMessage(baMessage, baPassword)
    val eomArray = byteArrayOf(0x0, 0x0, 0x3)
    // Note that we append the end-of-message markers
    // AFTER encrypting the message.
    // Append the end-of-message marker (3 bytes: 0, 0, 3) now.
    baEncryptedMessage += eomArray
    dfh.baEncryptedMessage = baEncryptedMessage
    dfh.baEndOfMessage = eomArray

    // Debug the following call carefully!
    dfh.encryptedMessageAsIntList = getBitsAsIntList(dfh.baEncryptedMessage.toString(Charsets.UTF_8))

    // Get the BufferedImage by reading the input file.
    // Make sure I'm populating the dfh structure with the data the
    // user has entered, etc.
    val bufferedImage: BufferedImage
    try {
        bufferedImage = ImageIO.read(inputFile)
        //var result: DataForHide = DataForHide()
        dfh.bufferedImage = bufferedImage
        dfh.resultString = RESULT_SUCCESS
        dfh.resultText = INPUT_FILE_READ_SUCCESSFULLY
    } catch (e: IIOException) { /*FileNotFoundException) {*/
        println(INPUT_FILE_DOES_NOT_EXIST)
        dfh.resultString = RESULT_FAILURE
        dfh.resultText = INPUT_FILE_DOES_NOT_EXIST
        // dfh.fileNames = Pair("", "")
        return dfh
    } // end catch block

    // Get the message to hide in the output file.
//    val msgToHide = getInputFromUser(HIDDEN_MESSAGE_PROMPT)
//    result.messageToHide = msgToHide
//    val msgBits = getBitsAsIntList(msgToHide)

    // Is the file large enough to hold the secret message?
    //fun msgIsTooLarge(bi: BufferedImage, msg: List<Int>): Boolean {
    if (msgIsTooLarge(bufferedImage, messageBits)) {
        println(MSG_IMAGE_TOO_SMALL)
        dfh.resultString = RESULT_FAILURE
        dfh.resultText = MESSAGE_TOO_LARGE
        return dfh
    }

    dfh.resultString = RESULT_SUCCESS
    dfh.resultText = INPUT_AND_OUTPUT_FILE_NAMES_NOW_AVAILABLE

    // I don't think password support is required,
    // at least not yet! If it _is_ needed, see above
    // code around line 281.
//    val password = getInputFromUser(PASSWORD_PROMPT)
    return dfh
} // end getDataForHide()

/**
 * I'll leave out password and decryption processing in
 * this function.
 *
 * @param bi a bufferedImage from a .png file
 */
fun getMessageBitsFromBufferedImage(bi: BufferedImage): List<Int> {
    val imgWidth = bi.width
    val imgHeight = bi.height
    var bitList = mutableListOf<Int>() // listOf<Int>()

    // Loop through the image (a BufferedImage) rows, 1 row at a time.
    for (y in 0 until imgHeight) {
        // Loop through the image row, 1 pixel at a time.
        for (x in 0 until imgWidth) {
            // const val END_OF_MESSAGE = "000000000000000000000011"
            val lastElems = bitList.takeLast(END_OF_MESSAGE.length).joinToString("")
            if (lastElems == END_OF_MESSAGE) {
                // We're done building a bit list (MutableList<Int>)
                // representing the hidden message.
                // return bitList.dropLast(END_OF_MESSAGE.length) //.toMutableList()
                return bitList // The caller, getHiddenMessageFromImage2(),
                               // expects the eom markers to be there!
            }
            bitList.add(bi.getRGB(x, y) and 1) // bitwise operation `and 1` returns the last bit.

        }
    }
    return bitList
} // end getMessageBitsFromBufferedImage()

/**
 * Note that stages prior to 3 don't require any passwords.
 *
 * Called from processShow()
 */
//fun getHiddenMessageFromImage(bufferedImage: BufferedImage, password: ByteArray): String {
fun getHiddenMessageFromImage2(dfs: DataForShow, bi: BufferedImage): String {
    // The following call is new and should be
    // debugged carefully! This call is the heart of
    // this function.
    val bitList = getMessageBitsFromBufferedImage(bi).toMutableList()
    //val bitList = getBitsAsIntList(str).toMutableList()
    // Is this right, or am I premature in dropping the last 24 bits?

     val lastElems = bitList.takeLast(END_OF_MESSAGE.length).joinToString("")
    if (lastElems == END_OF_MESSAGE) {
        // I think this call is to be changed and
        // replaced with a call to a new function
        // ... yada yada. Fix it next time. Now: 2023-02-28 21:36.

        val decryptedMsg =
            decryptMessage(
                getByteArrayFromIntList(bitList.dropLast(END_OF_MESSAGE.length)),
//                getByteArrayFromIntList(dfs.passwordBits)
                 dfs.baPassword
            )
        return decryptedMsg.toString(Charsets.UTF_8)
    } // end if...
    // If we get here, the image file doesn't contain a hidden message.
    return MSG_NOT_FOUND
} // end getHiddenMessageFromImage2()

fun getHiddenMessageFromImage(dfs: DataForShow,
                              bufferedImage: BufferedImage): String {
    val imgWidth = bufferedImage.width
    val imgHeight = bufferedImage.height
    val bitList = mutableListOf<Int>()

    val passwordBits = dfs.passwordBits // getBitsAsIntList(dfs.password)
    //val bitList = getBitsAsIntList(str).toMutableList()

    // Loop through the image (a BufferedImage) rows, 1 row at a time.
    for (y in 0 until imgHeight) {
        // Loop through the image row, 1 pixel at a time.
        for (x in 0 until imgWidth) {
            // const val END_OF_MESSAGE = "000000000000000000000011"
            val lastElems = bitList.takeLast(END_OF_MESSAGE.length).joinToString("")
            if (lastElems == END_OF_MESSAGE) {
                // We're done building a bit list (MutableList<Int>)
                // representing the hidden message.

                //if (lastElems == listOf(0x0, 0x0, 0x3).joinToString(""))
                //if (END_OF_MESSAGE in bitList.joinToString(""))
                // If we're looking at the end-of-message tokens, it's
                // time to return the hidden message. First, we remove
                // these tokens (eom markers).
                // Note: decodeFromBits() uses windowing.
                //bitList.dropLast(END_OF_MESSAGE.length)

                // The following call doesn't make sense -- we're trying
                // to get the message and show it to the user, so
                // dfs.baMessage is empty at this point. baPassword does
                // make sense, since the user enters the password for a
                // "show" command.
                decryptMessage2(
                    bitList.dropLast(END_OF_MESSAGE.length),
                    dfs.passwordBits
                )
                return decodeFromBits2(
                    // Remove the end-of-message tokens before converting
                    // the bit list (List<Int>) to a String (the hidden message).
                    // bitList.dropLast(END_OF_MESSAGE.length)
                    // Stage 4 requires encryption.
                    // fun decryptMessage(message: List<Int>, password: List<Int>): List<Int> =
                    bitList.dropLast(END_OF_MESSAGE.length)
                    // fix this mess up later!
// fun decryptMessage(message: ByteArray, password: ByteArray): ByteArray =
//                        decryptMessage (
//                            dfs.baMessage,
//                            dfs.baPassword
//                        )
                ) // end decodeFromBits2() call
            } // end if...
            // We build the List<Int> 1 pixel at a time.
            bitList.add(bufferedImage.getRGB(x, y) and 1) // bitwise operation `and 1` returns the last bit.
        } // end x-loop
        //println("Past end of x-loop")
    } // end y-loop

    // If we get here, the image file doesn't contain a hidden message.
    return MSG_NOT_FOUND
} // end getHiddenMessageFromImage()

fun messageFromImage(bufferedImage: BufferedImage,
                     password: List<Int>): String {
    val imgWidth = bufferedImage.width
    val imgHeight = bufferedImage.height
    val bitList = mutableListOf<Int>()

    for (y in 0 until imgHeight) {
        for (x in 0 until imgWidth) {
            if (END_OF_MESSAGE in bitList.joinToString(""))
                return decodeFromBits2(
                    decryptMessage2(
                        bitList.dropLast(END_OF_MESSAGE.length),
                        password
                    )
                )

            bitList.add(bufferedImage.getRGB(x, y) and 1)       // bitwise operation `and 1` returns the last bit
        }
    }

    return MSG_NOT_FOUND
} // end messageFromImage()

/**
 * Converts a string to a bit-representation, where each bit is an Integer with value `0` or `1`
 * @return List of integers
 */
fun encodeToBits(message: String): List<Int> {
    return message.encodeToByteArray()
        .map { eachByte ->
            eachByte
                .toBinaryString8()
                .map { it.digitToInt() }
        }
        .flatten()
} // end encodeToBits()

/**
 * Reads the hidden message from an image file and displays it
 */
fun cmdShow() {
    val inImgFile = File(getInputFromUser(MSG_INPUT_FILENAME))
    val password = encodeToBits(getInputFromUser(MSG_PWD))

    val inputImage: BufferedImage
    try {
        inputImage = ImageIO.read(inImgFile)
    } catch (e: IOException) {
        println("${e.message}")
        return
    }

    println("$MSG_SHOW\n${messageFromImage(inputImage, password)}")
} // end cmdShow()

/**
 * Encrypts the message with the password (message XOR password)
 * Running the function again will decrypt the message (encryptedMessage XOR password)
 *
 * @param message a List<Int> containing the message to be encrypted
 *                (each integer is a 0 or a 1)
 * @return List<Int> containing the bits of the encrypted message
 */
fun encryptMessage2(message: List<Int>, password: List<Int>): MutableList<Int> {
    val msg = mutableListOf<Int>()

    var pwdBit = 0
    message.forEach {
        msg.add(it xor password[pwdBit])
        if (pwdBit == password.lastIndex) pwdBit = 0 else pwdBit++
    }

    return msg
} // end encryptMessage2()

/**
 *  Decrypts the message with the password (message XOR password)
 *  @return List<Int> containing the bits of the decrypted message
 */
fun decryptMessage2(message: List<Int>, password: List<Int>): MutableList<Int> =
    encryptMessage2(message, password)

/**
 * Reads the bit representation of a string from an
 * integer (Int) list of '0's & '1's and converts the entire
 * sequence of bits to one string.
 *
 * @return string
 */
fun decodeFromBits(bitList: List<Int>): String {
    // We'll use windowing here.
    return bitList
        // We'll take 8 bits at a time and advance 8 bits
        // to get to the next chunk (window).
        .windowed(Byte.SIZE_BITS, Byte.SIZE_BITS)
        {
            it // is a List<Int>
                .joinToString("")
                .toByte(2)
        }
        .toByteArray()
        .toString(Charsets.UTF_8)
} // end decodeFromBits()

/*
 * Takes a List<Int> (a "bitList") containing integers that
 * are a 0 or a 1 and returns a string which that bitList
 * represents.
 *
 * @param bitList a List<Int> that represents a string
 * @return a String represented by the bitList
 */
fun decodeFromBits2(bitList: List<Int>): String {
    // We'll use windowing here.
    val windowedBitListList = bitList.windowed(Byte.SIZE_BITS, Byte.SIZE_BITS)
    //val windowedBitListStr = windowedBitList.joinToString("")
    var ba = byteArrayOf()
    val x =
        windowedBitListList.forEach { // "it" is a List<Int>
            println(it) // e.g., [0, 1, 0, 0, 1, 0, 0, 0]
            val aByte = // joined ex.: "01001000"
                it.joinToString("") // "it" ex.: [0, 1, 0, 0, 1, 0, 0, 0]
                  .toByte(2) // ex.: 72
            ba += aByte
        } // end forEach...
    val str = ba.toString(Charsets.UTF_8) // "".toString()

    return str
} // end decodeFromBits2()

/**
* Displays prompt message and reads user's answer from
* standard input.
* @return string representing user's typed answer
*/
fun getInputFromUser(promptMessage: String): String {
    println(promptMessage)
    return readln()
} // end getInputFromUser()

fun encryptMessage(message: ByteArray, password: ByteArray): ByteArray {
//    val windowedMessage = message.toList().windowed(password.size, password.size, true)
//    println(message.toUByteArray())
    // UByteArray(storage=[72, 101, 108, 108, 111, 44, 32, 121, 111, 117, 32, 98, 101, 97, 117, 116, 105, 102, 117, 108, 32, 112, 101, 111, 112, 108, 101, 46])
    //var baOut = "".encodeToByteArray()
//    var baOut = byteArrayOf().toMutableList()
    // Each window in the message is the password's length long.
    // We'll xor that amount of message bytes at a time.
//    var windowedMessageAsList = windowedMessage[0]
//    windowedMessage.forEach {// it: List<Byte>
//        // "it" is a list of bytes (one window).
//        var windowedMessageAsList = it.toMutableList()

//        for (k in 0 until windowedMessageAsList.size) {
//            windowedMessageAsList[k] = it[k].xor(password[k % password.size])
//        }

        for (k in 0 until message.size) {
            // message[k] = message[k].xor(password[k % password.size])
            message[k] = message[k].xor(password[k % password.size])
        }

    return message
} // end encryptMessage()


fun getBufferedImageOfAFile(fileName: String): BufferedImage {
    return ImageIO.read(File(fileName))
} // end getBufferedImageOfAFile()

fun getStringFromByteArray(ba: ByteArray): String {
    val str = ba.toString(Charsets.UTF_8)
    return str
} // end getStringFromByteArray()

/**
 * Takes a List<Int> (each integer is a 0 or 1) and
 * returns a ByteArray that's the equivalent of the
 * integer list.
 *
 * @param intList a List<Int> containing integers that
 *                are 0 or 1
 * @return a ByteArray that's the equivalent of the integer list
 */
fun getByteArrayFromIntList(intList: List<Int>): ByteArray {
    //var ba: ByteArray = byteArrayOf()

    return intList
        // We'll take 8 bits at a time and advance 8 bits
        // to get to the next chunk (window).
        .windowed(Byte.SIZE_BITS, Byte.SIZE_BITS)
        {
            it // is a List<Int>
                .joinToString("")
                .toByte(2)
        }
        .toByteArray()
        //.toString(Charsets.UTF_8)
} // end getByteArrayFromIntList()

/**
 *  Decrypts the message with the password (message XOR password)
 *  @return List<Int> containing the bits of the decrypted message
 *  @param message a ByteArray holding the message to be decrypted
 *  @param password a ByteArray containg the password used to
 *                  decrypt the message
 */
// fun decryptMessage(message: List<Int>, password: List<Int>): List<Int> =
fun decryptMessage(message: ByteArray, password: ByteArray): ByteArray =
    encryptMessage(message, password)

/**
 * Inserts the message bits into the least significant bits of the blue color bits.
 *
 * @return image (bufferedImage) with the message hidden inside it
 */
fun insertMessageBitsIntoImage(bufferedImage: BufferedImage, message: List<Int>): BufferedImage {
    val imgWidth = bufferedImage.width
    val imgHeight = bufferedImage.height
    var bitCount = 0

    loop@ for (y in 0 until imgHeight) {
        for (x in 0 until imgWidth) {
            if (bitCount == message.size) // We wrote all the message bits.
                break@loop
/*
            setBlueBit(bufferedImage, x, y, rgb, message[bitCount])
            bitCount++ // Advance to the next bit we need to set.

 */
            val rgb = bufferedImage.getRGB(x, y)
            setBlueBit(bufferedImage, x, y, rgb, message[bitCount])

            bitCount++ // Advance to the next bit we need to set.

//            if (rgb and 1 != message[bitCount]) // If blue's last bit doesn't match the bit we need
//                                                // to write,
//                bufferedImage.setRGB(x, y, rgb xor 1) // `xor 1` flips the bit value: 1->0 and 0->1.
//
//            bitCount++ // Move to the next bit we need to write.
        } // end x for loop
    } // end y for loop
    return bufferedImage
} // end insertMessageBitsIntoImage()

/*
 * @param bi  the BufferedImage that represents the image we're working with
 * @param x   the index of the pixel along the x axis (horizontal position)
 * @param y   the index of the pixel along the y axis
 * @param rgb the RGB of the pixel whose blue bit we're setting ( via getRGB() )
 * @param bit the integer value, 0 or 1, we're setting
 *            (from the secret message being hidden within the image)
 */
fun setBlueBit(bi: BufferedImage, x: Int, y: Int, rgb: Int, bit: Int) {
    val color = Color(rgb)
    val r = color.red
    val g = color.green
    val b = color.blue
    //val bit = message[bitCount]                                              // (otherwise, we're all set, do nothing)
    bi.setRGB(x, y, Color(r, g, b.and(254).or(bit) % 256).rgb)
} // end setBlueBit()

//fun addSecretMsgEndMarkers(mb: MutableList<Int>) {
fun addSecretMsgEndMarkers(ba: ByteArray): ByteArray {
    val array = byteArrayOf(0x0, 0x0, 0x3)
    var newba = byteArrayOf()
    newba = ba + array
    return newba
} // end addSecretMsgEndMarkers()

/* Doc for digitToInt() on kotlinlang.org
( https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/digit-to-int.html )
fun Char.digitToInt(): Int

Returns the numeric value of the decimal digit that this
Char represents. Throws an exception if this Char is not
a valid decimal digit.

A Char is considered to represent a decimal digit
if isDigit is true for the Char. In this case, the
Unicode decimal digit value of the character is returned.

import java.util.*
import kotlin.test.*

fun main(args: Array<String>) {
    println('5'.digitToInt()) // 5
    println('3'.digitToInt(radix = 8)) // 3
    println('A'.digitToInt(radix = 16)) // 10
    println('k'.digitToInt(radix = 36)) // 20

    // radix argument should be in 2..36
    // '0'.digitToInt(radix = 1) //  will fail
    // '1'.digitToInt(radix = 100) //  will fail
    // only 0 and 1 digits are valid for binary numbers
    // '5'.digitToInt(radix = 2) //  will fail
    // radix = 10 is used by default
    // 'A'.digitToInt() //  will fail
    // symbol '+' is not a digit in any radix
    // '+'.digitToInt() //  will fail
    // Only Latin letters are valid for digits greater than 9.
    // 'β'.digitToInt(radix = 36) //  will fail
}
*/

/**
 * Converts a string to a bit representation,
 * where each bit is an Integer with value `0` or `1`.
 *
 * Doc for the ByteArray.map() function:
 * inline fun <R  > ByteArray.map(transform: (Byte) -> R): List<R>
 *
 * @return List of integers
 */
fun getBitsAsIntList(message: String): List<Int> {
    return message.encodeToByteArray()
        .map { oneByte ->
            oneByte
                .toBinaryString8()
                .map { it.digitToInt() }
        }
        .flatten()
} // end getBitsAsIntList()

/**
* Converts a byte to its unsigned 8-bit string representation
 *
* @return 8-character string, left padded with zeros if necessary
*
* Notes:
*   1. Byte.SIZE_BITS is 8 (the number of bites in a Byte).
*   2. this.toString(2) gives us " 1100101" when the Byte
*      has a value of 101. The "replace" call replaces the
*      space on the left end, so we end up with "01100101".
*   3. ...so this Byte.toBinaryString8() extension function
*      is similar to Integer.to Integer.toBinaryString(myInt).
*      Unfortunately, that function doesn't return leading
*      zeros or spaces, although that could be handled
*      relatively easily (left-padding until we have 8
*      characters ( via the call to replace() ).
*   4. This is an extension function called on a Byte.
*/
fun Byte.toBinaryString8(): String {
    return String
        .format("%${Byte.SIZE_BITS}s", this.toString(TWO))
        .replace(' ', '0')
}

/**
 * Hides the message inside the copy of an existing image file
 *
 * Note that dfh.baEncryptedMessage holds the encrypted secret
 * message followed by 3 end-of-message marker bytes.
 */
fun saveMessageToHideInOutputFile(dfh: DataForHide): ResultData {
    // Let's iterate through the secret message that's
    // been encoded in a ByteArray that has been transformed
    // into a List<Int>. We need to iterate over the
    // picture file's pixels as well!
    var result: ResultData = ResultData(RESULT_SUCCESS, "ok")

    val inImgFile = File(dfh.inputFileName)
    val outImgFile = File(dfh.outputFileName)
    val message = dfh.messageBits
    val password = dfh.passwordBits

    val inputImage: BufferedImage
    try {
        inputImage = ImageIO.read(inImgFile)
    } catch (e: IOException) {
        println("${e.message}")
        return result
    }

    try {
        // Make sure I'm including the eom markers in this write!
        var msgBits = dfh.encryptedMessageAsIntList
        ImageIO.write(
            // Debug the following call carefully.
            // insertMessageBitsIntoImage(inputImage, dfh.encryptedMessageAsIntList),
            insertMessageBitsIntoImage(inputImage, msgBits),
            IMG_FILE_EXTENSION,
            outImgFile
        ) // end ImageIO.write()
        // Change the file path delimiter character from `\` (Windows) to `/` (Linux, macOS), otherwise unit test fails
        val msgGood = "$MESSAGE_SAVED_IN ${dfh.outputFileName} $MESSAGE_SAVED_IN_2."
        println(msgGood) // processHide() does this.
        result.resultString = RESULT_SUCCESS
        result.resultText = msgGood
    } catch (e: IOException) {
        result.resultString = RESULT_FAILURE
        result.resultText = "${e.message}"
        // println("${e.message}")
    }

    val bi = dfh.bufferedImage!!

    // Iterate over ByteArray, saving each _bit_ in the
    // least significant bit of the blue pixels in the
    // BufferedImage representing the picture file.
    // Note that the array of bytes has a 3-byte marker
    // at its end -- 3 bytes of 0, 0, and 3.

    return result
} // end saveMessageToHideInOutputFile()

// Learning/debugging...
fun printColor(theColor: Color) {
    val red = theColor.getRed()
    val green = theColor.getGreen()
    val blue = theColor.getBlue()
    val alpha = theColor.getAlpha()
    println("red: $red, green: $green, blue: $blue, alpha: $alpha")
}

/**
* Checks if the message is too big to fit inside the image.
* @return true, if the message is too large to hide in the
*               image
*/
fun msgIsTooLarge(bi: BufferedImage, msg: List<Int>): Boolean {
    return msg.size > bi.width * bi.height
} // end msgIsTooLarge()

// Here's a test image (512 x 512):
// Lenna_test_image.png
// random_number.png
// 2016-11-24_21-33-12.png
//
// This function can probably be removed!
fun saveOutputFile(outputFileName: String,
                   bufferedImage: BufferedImage): Pair<String, String> {
    var outResult: Pair<String, String>
    var outResultString: String
    return try {
        ImageIO.write(bufferedImage, "png", File(outputFileName))
        // println("$MESSAGE_SAVED_IN $outputFileName")
        // "Image yada.png is saved."
        // const val OUTPUT_FILE_IS_SAVED_FRAG_1 = "Image"
        // const val OUTPUT_FILE_IS_SAVED_FRAG_2 = "is saved."
        // println("${MESSAGE_SAVED_IN} $outputFileName ${MESSAGE_SAVED_IN_2}.")
        outResultString = "Success"
        outResult = Pair(outResultString, OUTPUT_FILE_WRITE_SUCCEEDED)
        outResult // Lifted out of the try block by IntelliJ IDEA.
    } catch (ex: Exception) {
        outResultString = "Failure"
        outResult = Pair(outResultString, ex.message!!)
        outResult // Lifted out of the try block by IntelliJ IDEA.
    }
} // end saveOutputFile()

/**
 * Experimental code for debugging, etc., will go in this function.
 */
fun tryIt() {
    val ba_message = "Hello, you beautiful people.".toByteArray()
    println("Calling getStringFromByteArray(ba) and printing the result.")
    println(getStringFromByteArray(ba_message))

    val messageStr = ba_message.toString(Charsets.UTF_8)
    println(messageStr)
    val msgBits = getBitsAsIntList(messageStr)
    // Seems to work right, but check it out:
    val baMsg = getByteArrayFromIntList(msgBits).toString(Charsets.UTF_8)
    // println(getBitsAsIntList(messageStr))
    val decodeResult = decodeFromBits2(msgBits)
    // decodeResult is "Hello,you beautiful people.", as hoped for!
    val password = "nashorn"
    val ba_password = password.toByteArray()
    val passwordBits = getBitsAsIntList((password))
    println(getByteArrayFromIntList(passwordBits).toString(Charsets.UTF_8))

    val aByte = 0b01010101.toByte()
    println(aByte.toBinaryString8()) // "01010101"

    // Do these yield the same results?
    val encryptedMessage = encryptMessage(ba_message, ba_password)
    val encryptedMessage2 = encryptMessage2(msgBits, getBitsAsIntList(password))
//getByteArrayFromIntList(decryptMessage2(encryptedMessage2, passwordBits)).toString(Charsets.UTF_8)
    // See decodeFromBits()
    val msgBitsStr = msgBits.joinToString("")
    println("msgBitsStr: $msgBitsStr")
    // java.lang.NumberFormatException:
//    println(msgBitsStr.toByte(2))

    // Yay. The following call seems to work!
    val myByteArray = getByteArrayFromIntList(msgBits)
// myByteArray.toString(Charsets.UTF_8)

    // Does this work? (Returns a List<Int>.) It seems to work.
    encryptMessage2(msgBits, passwordBits)

    val dfs = getDataForShow()
    // The following call sometimes throws an exception
    // from decodeFromBits2(). Let's avoid it for now.
//    getHiddenMessageFromImage(dfs, dfs.bufferedImage!!)

//    val fileName = "random_number_out1114.png"
    val fileName = "testimage_out4441.png"
    val bi = getBufferedImageOfAFile(fileName)
    val bList= getMessageBitsFromBufferedImage(bi)
    println("bList size: ${bList.size}")
//    for (i in 0..15) {
//        println(bList[i])
//    }

    // Experimental code:
    val msg = "Hello, you beautiful people."
    val pwd = "nashorn"
    println("The password is $pwd")
    for (k in 0 until msg.length) {
        println(pwd[k % pwd.length])
    }

    val stopHere = "STOP"
} // end tryIt()

/*
Example: How the user interface should work.

Task (hide, show, exit):
> hide
Hiding message in image.
Task (hide, show, exit):
> show
Obtaining message from image.
Task (hide, show, exit):
> task
Wrong task: task
Task (hide, show, exit):
> exit
Bye!
*/