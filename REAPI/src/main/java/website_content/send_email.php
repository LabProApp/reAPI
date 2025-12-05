<?php
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

require 'vendor/autoload.php';

$mail = new PHPMailer(true);

try {
    // Sanitize POST inputs
    $name = htmlspecialchars(strip_tags($_POST['name']));
    $email = filter_var($_POST['email'], FILTER_SANITIZE_EMAIL);
    $phone = htmlspecialchars(strip_tags($_POST['phone']));
    $message = htmlspecialchars(strip_tags($_POST['message']));

    // SMTP configuration
    $mail->isSMTP();
    $mail->Host       = 'smtp.gmail.com';  
    $mail->SMTPAuth   = true;
    $mail->Username   = 'meetthenikhil@gmail.com'; // Your Gmail
    $mail->Password   = 'ivwa skwo xlvg rqcp';     // Gmail App Password
    $mail->SMTPSecure = 'tls';
    $mail->Port       = 587;

    // Email headers
    $mail->setFrom('meetthenikhil@gmail.com', 'Website Contact');
    $mail->addAddress('abcd@gmail.com');  // Recipient

    // Email content
    $mail->isHTML(true);
    $mail->Subject = 'New Inquiry Form Submission';
    $mail->Body    = "
        <strong>Name:</strong> {$name}<br>
        <strong>Email:</strong> {$email}<br>
        <strong>Phone:</strong> {$phone}<br>
        <strong>Message:</strong> {$message}
    ";

    // Send email
    if ($mail->send()) {
        echo "success";  // AJAX expects this
    } else {
        echo "error";    // AJAX expects this
    }

} catch (Exception $e) {
    // On exception, return error to AJAX
    echo "error";
}
?>
