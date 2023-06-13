<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
</head>
<body>
<div><b>${emailMessage.message}</b></div>
<div style="margin-top: 10px">Добрый день, ${emailMessage.employeeName}!</div>
<div>Вам была назначена задача <b>"${emailMessage.taskName}"</b> в проекте <b>"${emailMessage.projectName}"</b></div>
<br>
<div><b>Крайний срок выполнения задачи:</b> ${emailMessage.deadlineTime} ${emailMessage.deadlineDate}</div>
<br/>
<div>Хорошего дня!</div>
</body>
</html>