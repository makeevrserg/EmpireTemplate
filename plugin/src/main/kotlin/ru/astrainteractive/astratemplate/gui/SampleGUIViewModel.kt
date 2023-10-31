package ru.astrainteractive.astratemplate.gui

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.bukkit.ChatColor
import org.bukkit.event.inventory.ClickType
import ru.astrainteractive.astralibs.async.AsyncComponent
import ru.astrainteractive.astratemplate.api.ItemStackSpigotAPI
import ru.astrainteractive.astratemplate.api.dto.UserDTO
import ru.astrainteractive.astratemplate.api.local.LocalApi
import ru.astrainteractive.astratemplate.gui.SampleGuiComponent.Model
import kotlin.random.Random

/**
 * MVVM/MVI technique
 */
class SampleGUIViewModel(
    private val localApi: LocalApi,
    private val itemStackSpigotAPi: ItemStackSpigotAPI
) : AsyncComponent(), SampleGuiComponent {

    override val model = MutableStateFlow<Model>(Model.Loading)

    override val randomColor: ChatColor
        get() = ChatColor.values()[Random.nextInt(ChatColor.values().size)]

    override fun onModeChange() {
        componentScope.launch(Dispatchers.IO) {
            println("OnModeChanged")
            when (model.value) {
                Model.Loading -> return@launch
                is Model.Items -> loadUsersModel()
                is Model.Users -> loadItemsModel()
            }
        }
    }

    override fun onItemClicked(slot: Int, clickType: ClickType) {
        when (val state = model.value) {
            Model.Loading -> {}
            is Model.Items -> {
                onItemStackClicked(slot)
            }

            is Model.Users -> {
                onPlayerHeadClicked(slot, clickType)
            }
        }
    }

    override fun onAddUserClicked() {
        componentScope.launch(Dispatchers.IO) {
            localApi.insertUser(UserDTO(-1, "id${Random.nextInt(20000)}", "mine${Random.nextInt(5000)}"))
            loadUsersModel()
        }
    }

    private fun onPlayerHeadClicked(slot: Int, clickType: ClickType) {
        val state = model.value as? Model.Users ?: return
        val users = state.users
        val user = users.getOrNull(slot) ?: return
        componentScope.launch(Dispatchers.IO) {
            when (clickType) {
                ClickType.MIDDLE -> localApi.updateUser(user)
                ClickType.LEFT -> localApi.deleteUser(user)
                else -> {
                    println(localApi.selectRating(user))
                    localApi.insertRating(user)
                }
            }
            loadUsersModel()
        }
    }

    private fun onItemStackClicked(slot: Int) {
        val state = model.value as? Model.Items ?: return

        val list = state.items.toMutableList()
        val item = list.getOrNull(slot)?.clone()?.apply {
            editMeta {
                it.setDisplayName(randomColor.toString() + this.type.name)
            }
        } ?: return
        list[slot] = item
        this.model.update {
            state.copy(items = list)
        }
    }

    private suspend fun loadItemsModel() {
        model.value = Model.Items(itemStackSpigotAPi.randomItemStackList())
    }

    private suspend fun loadUsersModel() {
        model.value = Model.Users(localApi.getAllUsers())
    }

    override fun onUiCreated() {
        componentScope.launch(Dispatchers.IO) {
            delay(1000)
            loadItemsModel()
        }
    }
}
