#!/bin/bash

PORT=6128

echo 'Зарегистрируем пользователя'
RESPONSE=$(curl --location "http://localhost:${PORT}/register" \
           --header 'Content-Type: application/json' \
           --data '{
               "login": "user",
               "password": "user",
               "name": "name",
               "surname": "surname",
               "birthDate": "2000-01-01",
               "sex": "MALE"
           }')
echo 'Сохраним логин пользователя'
USER_TOKEN=$(echo "$RESPONSE" | jq -r '.token')

echo 'Залогиним админа'
RESPONSE=$(curl --location "http://localhost:${PORT}/login" \
             --header 'Content-Type: application/json' \
             --data '{
                 "login": "admin",
                 "password": "admin"
             }')
echo 'Сохраним логин админа'
ADMIN_TOKEN=$(echo "$RESPONSE" | jq -r '.token')

echo '-----------ТЕСТИРОВАНИЕ НОВОСТЕЙ-----------'

echo 'Создадим новость'
curl --location "http://localhost:${PORT}/news" \
--header 'Content-Type: application/json' \
--header "Authorization: Bearer $USER_TOKEN" \
--data '{
    "text": "Моя первая новость",
    "photoPaths": []
}' | jq .
echo ''

echo 'Просмотрим новости'
curl --location "http://localhost:${PORT}/news" | jq .
echo ''

echo 'Отредактируем новость'
curl --location --request PUT "http://localhost:${PORT}/news/2" \
--header 'Content-Type: application/json' \
--header "Authorization: Bearer ${USER_TOKEN}" \
--data '{
    "text": "Моя отредактированная первая новость",
    "photoPaths": []
}' | jq .

echo 'Просмотрим новость'
curl --location "http://localhost:${PORT}/news/2" | jq .
echo ''

echo 'Добавим картинку к новости'

IMAGE_PATH_RESPONSE=$(curl --location "http://localhost:${PORT}/photo" \
--header "Authorization: Bearer ${USER_TOKEN}" \
--form 'file=@"testImage.jpg"')

IMAGE_PATH=$(echo "$IMAGE_PATH_RESPONSE" | jq -r '.path')

curl --location --request PUT "http://localhost:${PORT}/news/2" \
--header 'Content-Type: application/json' \
--header "Authorization: Bearer ${USER_TOKEN}" \
--data "{
    \"text\": \"Моя отредактированная первая новость\",
    \"photoPaths\": [\"${IMAGE_PATH}\"]
}" | jq .

echo 'Просмотрим новость'
curl --location "http://localhost:${PORT}/news/2" | jq .
echo ''

echo 'Удалим новость'
curl --location --request DELETE "http://localhost:${PORT}/news/2" \
--header "Authorization: Bearer ${USER_TOKEN}" | jq .

echo 'Просмотрим все новости'
curl --location "http://localhost:${PORT}/news" | jq .
echo ''

echo '-----------ТЕСТИРОВАНИЕ ПОДПИСОК-----------'
echo 'Посмотрим список доступных подписок'
curl --location "http://localhost:${PORT}/subscriptions/available" \
--header "Authorization: Bearer ${USER_TOKEN}" | jq .
echo ''

echo 'Оформим бесплатную подписку'
curl --location "http://localhost:${PORT}/subscriptions/subscribe" \
--header 'Content-Type: application/json' \
--header "Authorization: Bearer ${USER_TOKEN}" \
--data '{
    "subscriptionName": "Dating",
    "monthCount": 12,
    "bankDetails": {}
}' | jq .
echo ''

echo 'Оформим платную подписку'
curl --location "http://localhost:${PORT}/subscriptions/subscribe" \
--header 'Content-Type: application/json' \
--header "Authorization: Bearer ${USER_TOKEN}" \
--data '{
    "subscriptionName": "Music",
    "monthCount": 12,
    "bankDetails": {
        "bankAccountName": "MY NAME",
        "bankAccountSurname": "MY SURNAME",
        "cardNumber": "1234567812345678",
        "validityPeriod": "04/32",
        "cvv": "777"
    }
}' | jq .
echo ''

echo 'Просмотрим активные подписки аккаунта'
curl --location "http://localhost:${PORT}/subscriptions" \
--header "Authorization: Bearer ${USER_TOKEN}" | jq .
echo ''

echo 'Отменим платную подписку'
curl --location --request DELETE "http://localhost:${PORT}/subscriptions/unsubscribe" \
--header 'Content-Type: application/json' \
--header "Authorization: Bearer ${USER_TOKEN}" \
--data '{
    "subscriptionName": "Music"
}' | jq .

echo 'Просмотрим активные подписки аккаунта'
curl --location "http://localhost:${PORT}/subscriptions" \
--header "Authorization: Bearer ${USER_TOKEN}" | jq .
echo ''

echo '-----------ТЕСТИРОВАНИЕ УПРАВЛЕНИЯ ПРОФИЛЕМ-----------'

echo 'Посмотрим все профили'
curl --location "http://localhost:${PORT}/profiles" | jq .
echo ''

echo 'Посмотрим профиль пользователя'
curl --location "http://localhost:${PORT}/profiles/user" | jq .
echo ''

echo 'Получим список языков'
curl --location "http://localhost:${PORT}/languages" | jq .
echo ''

echo 'Установим информацию о профиле и добавим пользователя admin в семью'
curl --location --request PUT "http://localhost:${PORT}/profiles/3" \
--header 'Content-Type: application/json' \
--header "Authorization: Bearer ${USER_TOKEN}" \
--data '{
    "login": "user",
    "shortDescription": "updated",
    "homeTown": null,
    "knownLanguages": ["Русский"],
    "maritalStatus": null,
    "familyLogins": ["admin"]
}' | jq .
echo ''

echo 'Посмотрим все профили'
curl --location "http://localhost:${PORT}/profiles" | jq .
echo ''

echo 'Посмотрим персональные данные пользователя'
curl --location "http://localhost:${PORT}/accounts" \
--header "Authorization: Bearer ${USER_TOKEN}" | jq .
echo ''

echo 'Отправим запрос на изменение имени'
curl --location --request PUT "http://localhost:${PORT}/accounts" \
--header 'Content-Type: application/json' \
--header "Authorization: Bearer ${USER_TOKEN}" \
--data '{
    "name": "newName",
    "surname": "surname"
}'
echo ''

echo 'Админом получим все запросы на изменение персональных данных'
curl --location "http://localhost:${PORT}/admin/requests" \
--header "Authorization: Bearer ${ADMIN_TOKEN}" | jq .
echo ''

echo 'Одобрим изменение'
curl --location --request PUT "http://localhost:${PORT}/admin/requests/1/accept" \
--header "Authorization: Bearer ${ADMIN_TOKEN}" | jq .
echo ''

echo 'Посмотрим профиль пользователя'
curl --location "http://localhost:${PORT}/profiles/user" | jq .
echo ''

echo 'Посмотрим персональные данные пользователя'
curl --location "http://localhost:${PORT}/accounts" \
--header "Authorization: Bearer ${USER_TOKEN}" | jq