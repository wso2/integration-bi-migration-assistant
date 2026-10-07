import ballerina/ftp;

ftp:Client Archive_SftpConnection = checkpanic new ({protocol: ftp:SFTP, host: "sftp.archive.example.com", port: Archive_SftpConnection_port, auth: Resources_SFTP_Archive_UsePrivateKey ? {credentials: {username: Resources_SFTP_Username}, privateKey: {path: Resources_SFTP_Archive_PrivateKey, password: Resources_SFTP_Archive_PrivateKeyPassword}} : {credentials: {username: Resources_SFTP_Username, password: Resources_SFTP_Archive_Password}}});
ftp:Client Orders_SftpConnection = checkpanic new ({protocol: ftp:SFTP, host: Resources_SFTP_Orders_Host, port: Resources_SFTP_Orders_Port, auth: {credentials: {username: Resources_SFTP_Username, password: Resources_SFTP_Orders_Password}}});
ftp:Client Reports_SftpConnection = checkpanic new ({protocol: ftp:SFTP, host: "sftp.reports.example.com", port: 22, auth: {credentials: {username: Resources_SFTP_Username}, privateKey: {path: Resources_SFTP_Reports_PrivateKey, password: Resources_SFTP_Reports_PrivateKeyPassword}}});
