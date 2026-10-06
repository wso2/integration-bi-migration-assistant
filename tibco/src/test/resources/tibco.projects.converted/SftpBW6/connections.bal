import ballerina/ftp;

ftp:Client Orders_SftpConnection = checkpanic new ({protocol: ftp:SFTP, host: Resources_SFTP_Orders_Host, port: Resources_SFTP_Orders_Port, auth: {credentials: {username: Resources_SFTP_Username, password: Resources_SFTP_Orders_Password}}});
ftp:Client Reports_SftpConnection = checkpanic new ({protocol: ftp:SFTP, host: "sftp.reports.example.com", port: 22, auth: {credentials: {username: Resources_SFTP_Username}, privateKey: {path: Resources_SFTP_Reports_PrivateKey, password: Resources_SFTP_Reports_PrivateKeyPassword}}});
