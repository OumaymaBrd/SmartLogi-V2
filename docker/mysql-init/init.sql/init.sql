-- On autorise root à se connecter de n'importe où avec le bon mot de passe
ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY 'root_password';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;