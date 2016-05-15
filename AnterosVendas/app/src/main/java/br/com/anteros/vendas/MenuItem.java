/*
 * Copyright 2016 Anteros Tecnologia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.anteros.vendas;

import android.graphics.drawable.Drawable;

/**
 * Created by eduardogreco on 5/10/16.
 */
public class MenuItem {
    private long id;
    private String title;
    private String descricao;
    private String descricao2;
    private Drawable icon;
    private int cor;

    public MenuItem(long id, String title, String descricao, String descricao2, Drawable icon, int cor) {
        super();
        this.id = id;
        this.title = title;
        this.descricao = descricao;
        this.descricao2 = descricao2;
        this.icon = icon;
        this.cor = cor;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao2() {
        return descricao2;
    }

    public void setDescricao2(String descricao2) {
        this.descricao2 = descricao2;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public int getCor() {
        return cor;
    }

    public void setCor(int cor) {
        this.cor = cor;
    }
}
