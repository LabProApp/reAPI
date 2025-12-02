<?php
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

require 'vendor/autoload.php';

$mail = new PHPMailer(true);

try {
            $mail->isSMTP();
                $mail->Host       = 'smtp.gmail.com';  // Or your SMTP server
                $mail->SMTPAuth   = true;
                    $mail->Username   = 'meetthenikhil@gmail.com';
                    $mail->Password   = 'ivwa skwo xlvg rqcp'; // Gmail App password
                        $mail->SMTPSecure = 'tls';
                        $mail->Port       = 587;

                            $mail->setFrom('meetthenikhil@gmail.com', 'Website Contact');
                            $mail->addAddress('abcd@gmail.com');  // Recipient

                                $mail->isHTML(true);
                                $mail->Subject = 'New Inquiry Form Submission';
                                    $mail->Body    = "Name: {$_POST['name']}<br>Email: {$_POST['email']}<br>Phone: {$_POST['phone']}<br>Message: {$_POST['message']}";

                                    $mail->send();
                                        echo 'Message has been sent';
} catch (Exception $e) {
            echo "Message could not be sent. Mailer Error: {$mail->ErrorInfo}";
}
?>