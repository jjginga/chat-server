package org.academiadecodigo.ghostbugsters.server.commands;

import org.academiadecodigo.ghostbugsters.server.Server;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

public class Help implements Commands{
    @Override
    public String name() {
        return "/help";
    }

    @Override
    public String description() {
        String path;
        path =this.getClass().getPackage().toString().replace(".", "/").replace("package ","/");
        path = "src"+path+"/";
        File file = new File(path);
        String[] files = file.list();

        for (int i = 0 ; i<files.length; i++){
            files[i]=files[i].replace(".java","");

        }


        try {

        for (int i = 0 ; i < files.length; i++){

            System.out.println(this.getClass().getPackage().toString()+"."+files[i]);
            Class c = Class.forName(this.getClass().getPackage().toString().replace("package", "")+"."+files[i]);
            Commands command1 = (Commands) c.getConstructor().newInstance();
            Commands command = (Commands)Class.forName(path+files[i]).getConstructor().newInstance();
            System.out.println(command.name()+" : "+command.description());

        }

        } catch (ClassNotFoundException e) {
        e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


        return "List all commands: /help";
    }

    @Override
    public void implementation(Server server, String userName, String command) {

        //TODO implement, get package, list files.
    }


    public static void main(String[] args) {
        Help help = new Help();
        help.description();
    }
}
