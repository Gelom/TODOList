package com.uabart.todolist.gui;

import com.uabart.todolist.entity.Category;
import com.uabart.todolist.entity.Options;
import com.uabart.todolist.entity.Task;
import com.uabart.todolist.entity.TaskHolder;
import com.uabart.todolist.gui.components.FieldButtonName;
import com.uabart.todolist.gui.components.FieldCompletedCheckbox;
import com.uabart.todolist.gui.components.FieldIcon;
import com.uabart.todolist.gui.components.FieldMainName;
import com.uabart.todolist.gui.components.MyButton;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import codechicken.nei.Button;
import codechicken.nei.ItemPanel;
import codechicken.nei.Label;
import codechicken.nei.LayoutManager;
import codechicken.nei.TextField;
import codechicken.nei.Widget;

public class Layout {

    private MyButton previousPage, nextPage, addTask, addCategory, back;
    private List<Widget> toDraw;
    private HashMap<Task, List<Widget>> widgetMap;
    private TaskHolder holder;
    private GuiListener listener;
    private boolean getFocus = false;
    private Label cztLalka;

    public Layout() {
        toDraw = new ArrayList<Widget>();
        widgetMap = new HashMap<Task, List<Widget>>();
    }

    private void sendMessage(GuiMessage toSend) {
        sendMessage(toSend, null);
    }

    private void sendMessage(GuiMessage toSend, Task task) {
        listener.update(toSend, task);
    }

    public void setListener(GuiListener listener) {
        this.listener = listener;
    }

    public void init(GuiContainer gui, TaskHolder holder) {
        this.holder = holder;

        boolean edgeAlign = true;
//		boolean edgeAlign = NEIClientConfig.getBooleanSetting("options.edge-align buttons");
        int offsetx = edgeAlign ? 0 : 3;

        cztLalka = new Label("czt lalka", false);

        previousPage = new MyButton("<") {

            @Override
            public boolean onButtonPress(boolean rightclick) {
                sendMessage(GuiMessage.PREVIOUS_PAGE);
                return true;
            }

            @Override
            public String getButtonTip() {
                return "Previous page";
            }
        };
        previousPage.h = 20;
        previousPage.w = previousPage.contentWidth() + 6;
        previousPage.x = 0;
        previousPage.y = 20;
        // -------------------------------
        addTask = new MyButton("Add task") {

            @Override
            public boolean onButtonPress(boolean rightclick) {
                sendMessage(GuiMessage.ADD_TASK);
                return true;
            }
        };
        addTask.h = 20;
        addTask.x = previousPage.x + previousPage.w + offsetx;
        addTask.y = previousPage.y;
        // -------------------------------
        addCategory = new MyButton("Add category") {

            @Override
            public boolean onButtonPress(boolean rightclick) {
                sendMessage(GuiMessage.ADD_CATEGORY);
                return true;
            }
        };
        addCategory.h = 20;
        addCategory.x = previousPage.x + previousPage.w + offsetx;
        addCategory.y = previousPage.y;
        // -------------------------------
        int wt = Math.max(addTask.contentWidth(), addCategory.contentWidth()) + 6;
        addTask.w = wt;
        addCategory.w = wt;
        // --------------------
        back = new MyButton("Back") {

            @Override
            public boolean onButtonPress(boolean rightclick) {
                sendMessage(GuiMessage.BACK);
                return false;
            }
        };
        back.h = addTask.h;
        back.w = back.contentWidth() + 6;
        back.x = addTask.x + offsetx + wt;
        back.y = addTask.y;
        // -------------------------------
        nextPage = new MyButton(">") {

            @Override
            public boolean onButtonPress(boolean rightclick) {
                sendMessage(GuiMessage.NEXT_PAGE);
                return true;
            }

            @Override
            public String getButtonTip() {
                return "Next page";
            }
        };
        nextPage.h = 20;
        nextPage.w = nextPage.contentWidth() + 6;
        nextPage.x = back.x + back.w + offsetx;
        nextPage.y = addTask.y;
    }

    public void showCategory(final Category category, int currentPage) {
        // Cleaer current screen
        toDraw.clear();

        // alignment check
        boolean edgeAlign = true;
//		boolean edgeAlign = NEIClientConfig.getBooleanSetting("options.edge-align buttons");
        int offsety = edgeAlign ? 0 : 3;

        FieldMainName categoryName = new FieldMainName(category);
        categoryName.x = addTask.x;
        categoryName.y = 40;
        categoryName.w = 100;
        categoryName.h = back.h;

        Button categoryDelete = new MyButton("x") {
            @Override
            public boolean onButtonPress(boolean rightclick) {
                sendMessage(GuiMessage.DELETE, category);
                return true;
            }

            @Override
            public String getButtonTip() {
                return "Delete category";
            }
        };
        categoryDelete.x = previousPage.x;
        categoryDelete.y = categoryName.y;
        categoryDelete.h = categoryName.h;
        categoryDelete.w = categoryDelete.contentWidth() + 6;

        int count = 0;
        // first we display the non-completed tasks
        List<Task> active = category.getActiveTasks();
        for (int i = currentPage * Options.getInstance().getMaxTasksOnScreen(); i < active.size() && count < Options.getInstance().getMaxTasksOnScreen(); i++) {
            Task t = active.get(i);

            FieldCompletedCheckbox checkbox = new FieldCompletedCheckbox(t) {
                @Override
                public boolean onButtonPress(boolean rightclick) {
                    sendMessage(GuiMessage.COMPLETE, getTask());
                    return true;
                }
            };
            checkbox.y = 60 + offsety + count * 20;
            checkbox.h = 17;
            checkbox.w = checkbox.contentWidth() + 6;
            checkbox.x = previousPage.x;

            FieldButtonName textfield = new FieldButtonName(t, 17, 100) {
                @Override
                public boolean onButtonPress(boolean rightclick) {
                    sendMessage(GuiMessage.SELECT, this.getTask());
                    return true;
                }
            };
            textfield.x = addTask.x;
            textfield.y = checkbox.y;

            count++;
            toDraw.add(checkbox);
            toDraw.add(textfield);
        }

        boolean drawCompleted = Options.getInstance().showCompletedTasks();

        // only then we show the completed ones
        List<Task> completed = category.getCompletedTasks();
        if (drawCompleted) {
            for (int i = currentPage * Options.getInstance().getMaxTasksOnScreen() - active.size() + count; i < completed.size() && count < Options.getInstance().getMaxTasksOnScreen(); i++) {
                Task t = completed.get(i);

                FieldCompletedCheckbox checkbox = new FieldCompletedCheckbox(t) {
                    @Override
                    public boolean onButtonPress(boolean rightclick) {
                        sendMessage(GuiMessage.COMPLETE, getTask());
                        return true;
                    }
                };
                checkbox.y = 60 + offsety + count * 20;
                checkbox.h = 17;
                checkbox.w = checkbox.contentWidth() + 6;
                checkbox.x = previousPage.x;

                FieldButtonName textfield = new FieldButtonName(t, 17, 100) {
                    @Override
                    public boolean onButtonPress(boolean rightclick) {
                        sendMessage(GuiMessage.SELECT, this.getTask());
                        return true;
                    }
                };
                textfield.x = addTask.x;
                textfield.y = checkbox.y;

                count++;
                toDraw.add(checkbox);
                toDraw.add(textfield);
            }
        }

        // Disable previous/next page buttons
        if ((active.size() + (drawCompleted ? completed.size() : 0)) > currentPage * Options.getInstance().getMaxTasksOnScreen() + Options.getInstance().getMaxTasksOnScreen())
            nextPage.state = 0;
        else
            nextPage.state = 2;

        if (currentPage > 0)
            previousPage.state = 0;
        else
            previousPage.state = 2;

        // Add basic buttons
        if (category instanceof Category.Any)
            categoryName.setEditable(false);
        else
            toDraw.add(categoryDelete);

        toDraw.add(categoryName);
        toDraw.add(previousPage);
        toDraw.add(nextPage);
        toDraw.add(addTask);
        toDraw.add(back);
    }

    public void showTask(final Task task) {

        toDraw.clear();

        FieldMainName mainName = new FieldMainName(task);
        mainName.x = addTask.x;
        mainName.y = 40;
        mainName.w = 100;
        mainName.h = addTask.h;

        FieldIcon icon = new FieldIcon(task) {
            @Override
            public void onGuiClick(int mousex, int mousey) {
                if (LayoutManager.itemPanel.contains(mousex, mousey) && changing) {
                    ItemPanel.ItemPanelSlot item = LayoutManager.itemPanel.getSlotMouseOver(mousex, mousey);
                    if (item != null) {
                        ItemStack stack2 = item.item;
                        task.setReference(stack2);

                        if (task.getName().isEmpty() || task.getName().equals("Empty"))
                            task.setName("Make " + (stack2.getDisplayName().substring(0, 1).matches("[aeiouAEIOU]") ? "an" : "a") + " " + stack2.getDisplayName());

                        changing = false;
                    }
                }
            }
        };
        icon.x = mainName.x + mainName.w + 4;
        icon.y = mainName.y + 2;
        icon.w = 18;
        icon.h = 18;

        FieldCompletedCheckbox checkbox = new FieldCompletedCheckbox(task) {
            @Override
            public boolean onButtonPress(boolean rightclick) {
                sendMessage(GuiMessage.COMPLETE, getTask());
                return false;
            }
        };
        checkbox.w = checkbox.contentWidth() + 6;
        checkbox.h = 11;
        checkbox.y = mainName.y - 1;
        checkbox.x = previousPage.x;

        Button delete = new MyButton("x") {
            @Override
            public boolean onButtonPress(boolean rightclick) {
                sendMessage(GuiMessage.DELETE, task);
                return true;
            }

            @Override
            public String getButtonTip() {
                return "Delete task";
            }
        };
        delete.x = checkbox.x;
        delete.y = checkbox.y + checkbox.h;
        delete.h = checkbox.h;
        delete.w = delete.contentWidth() + 6;

        toDraw.add(mainName);
        toDraw.add(checkbox);
        toDraw.add(icon);
        toDraw.add(delete);
        if (Math.random() < 0.01) {
            toDraw.add(cztLalka);
        }

        int n = 0;
        for (final Task sub : task.listSubtasks()) {
            if (!sub.isCompleted() || Options.getInstance().showCompletedTasks()) {
                FieldMainName subName = new FieldMainName(sub) {
                    @Override
                    public boolean handleClick(int mousex, int mousey, int button) {
                        if (button == 2) {
                            sendMessage(GuiMessage.SELECT, this.getTask());
                            return true;
                        }
                        return super.handleClick(mousex, mousey, button);
                    }
                };
                subName.x = mainName.x;
                subName.y = mainName.y + (n + 2) * mainName.h + n;
                subName.w = mainName.w;
                subName.h = mainName.h - 2;
                if (getFocus)
                    subName.setFocus(true);

                FieldCompletedCheckbox subCheckbox = new FieldCompletedCheckbox(sub) {
                    @Override
                    public boolean onButtonPress(boolean rightclick) {
                        sendMessage(GuiMessage.COMPLETE, getTask());
                        return true;
                    }
                };
                subCheckbox.w = checkbox.w;
                subCheckbox.h = checkbox.h - 1;
                subCheckbox.y = subName.y - 1;
                subCheckbox.x = subName.x - 1 - subCheckbox.w;

                Button subDelete = new MyButton("x") {

                    @Override
                    public boolean onButtonPress(boolean rightclick) {
                        sendMessage(GuiMessage.DELETE, sub);
                        return true;
                    }

                    @Override
                    public String getButtonTip() {
                        return "Delete sub-task";
                    }
                };
                subDelete.x = subCheckbox.x;
                subDelete.y = subCheckbox.y + subCheckbox.h;
                subDelete.h = subCheckbox.h;
                subDelete.w = subCheckbox.w;

                FieldIcon subicon = new FieldIcon(sub) {
                    @Override
                    public void onGuiClick(int mousex, int mousey) {
                        if (LayoutManager.itemPanel.contains(mousex, mousey) && changing) {
                            ItemPanel.ItemPanelSlot item = LayoutManager.itemPanel.getSlotMouseOver(mousex, mousey);
                            if (item != null) {
                                ItemStack stack2 = item.item;
                                sub.setReference(stack2);

                                if (sub.getName().isEmpty() || sub.getName().equals("Empty"))
                                    sub.setName("Make " + (stack2.getDisplayName().substring(0, 1).matches("[aeiouAEIOU]") ? "an" : "a") + " " + stack2.getDisplayName());

                                changing = false;
                            }
                        }
                    }
                };
                subicon.x = icon.x;
                subicon.y = subName.y + 2;
                subicon.h = icon.h - 2;
                subicon.w = icon.w - 2;
                subicon.offset = 1;

                toDraw.add(subDelete);
                toDraw.add(subCheckbox);
                toDraw.add(subName);
                toDraw.add(subicon);
                n++;
            }
        }

        if (!task.isCompleted()) {
            TextField newSub = new TextField("Empty") {
                private boolean changed = false;

                @Override
                public void onTextChange(String oldText) {
                    if (!changed) {
                        getFocus = true;
                        changed = true;
                        sendMessage(GuiMessage.ADD_TASK);
                    }
                }

                @Override
                public List<String> handleTooltip(int mx, int my, List<String> tooltip) {
                    if (!contains(mx, my))
                        return tooltip;

                    tooltip.add("Right click to enable");
                    return tooltip;
                }

                @Override
                public void setFocus(boolean focus) {
                }

            };
            newSub.x = mainName.x;
            newSub.y = mainName.y + (n + 2) * mainName.h + n;
            newSub.w = mainName.w;
            newSub.h = mainName.h;
            toDraw.add(newSub);
        }

        getFocus = false;

        toDraw.add(back);

    }

    public void showMain(int currentPage) {

        // Clear current screen
        toDraw.clear();

        // alignment check
        boolean edgeAlign = true;
//		boolean edgeAlign = NEIClientConfig.getBooleanSetting("options.edge-align buttons");
        int offsety = edgeAlign ? 0 : 3;

        int count = 0;
        // first we display the non-completed tasks
        for (int i = currentPage * Options.getInstance().getMaxTasksOnScreen(); i < holder.getCategories().size() && count < Options.getInstance().getMaxTasksOnScreen(); i++, count++) {
            Category c = holder.getCategories().get(i);

            FieldButtonName textfield = new FieldButtonName(c, 17, 100) {
                @Override
                public boolean onButtonPress(boolean rightclick) {
                    sendMessage(GuiMessage.SELECT, this.getTask());
                    return true;
                }
            };
            textfield.x = addTask.x;
            textfield.y = 40 + offsety + count * 20;

            toDraw.add(textfield);
        }

        // Disable previous/next page buttons
        if ((holder.getCategories().size()) > currentPage * Options.getInstance().getMaxTasksOnScreen() + Options.getInstance().getMaxTasksOnScreen())
            nextPage.state = 0;
        else
            nextPage.state = 2;

        if (currentPage > 0)
            previousPage.state = 0;
        else
            previousPage.state = 2;

        // Add basic buttons
        toDraw.add(previousPage);
        toDraw.add(nextPage);
        toDraw.add(addCategory);

    }

    public List<Widget> getToDraw() {
        return toDraw;
    }
}