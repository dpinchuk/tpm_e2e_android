package com.example.tpm_e2e_android.constants

/**
 * 🟦 Обʼєкт із тестовими обліковими даними для Fixator.
 *     ⚠️ У бойових проєктах паролі краще зберігати в CI-секретах /
 *        instrumentationArgs, а не в коді репозиторію.
 */
object FixatorCredentials {

    // 🟦 Адмін
    const val ADMIN_LOGIN = "sergey@basov.in.ua"
    const val ADMIN_PASSWORD = "FixatorNEW1@"

    // 🟦 Звичайний користувач
    const val USER_LOGIN = "user1@user.com"
    const val USER_PASSWORD = "dmss111278D!"

    // 🟦 Офіційна особа
    const val OFFICIAL_LOGIN = "fff@fgh.com"
    const val OFFICIAL_PASSWORD = "dmss111278D!"

    // 🟦 Діловод
    const val CLERK_LOGIN = "dil@dil.com"
    const val CLERK_PASSWORD = "dmss111278D!"
}
