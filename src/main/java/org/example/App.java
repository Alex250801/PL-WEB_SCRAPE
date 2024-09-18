package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

class Player{
    String name;
    String base_url = "https://www.premierleague.com/stats/top/players/";
    String goals_url;
    String assists_url;
    String shots_url;
    String through_ball_url;
    String shots_on_target;
    String big_chances_missed;
    String interceptions;
    String blocks;
    String tackles;
    String errors_leading_to_goal;
    String clean_sheets;
    String goals_conceded;
    String saves;
    String penalty_saves;

    Player(String name){
        this.name = name;
        this.goals_url = this.base_url + "goals";
        this.assists_url = this.base_url + "goal_assist";
        this.shots_url = this.base_url + "total_scoring_att";
        this.through_ball_url = this.base_url + "total_through_ball";
        this.shots_on_target = this.base_url + "ontarget_scoring_att";
        this.big_chances_missed = this.base_url + "big_chance_missed";
        this.interceptions = this.base_url + "interception";
        this.blocks = this.base_url + "outfielder_block";
        this.tackles = this.base_url + "total_tackle";
        this.errors_leading_to_goal = this.base_url + "error_lead_to_goal";
        this.clean_sheets = this.base_url + "clean_sheet";
        this.goals_conceded = this.base_url + "goals_conceded?po=GOALKEEPER";
        this.saves = this.base_url + "saves?po=GOALKEEPER";
        this.penalty_saves = this.base_url + "penalty_save?po=GOALKEEPER";


    }
}

public class App {

    public static String GetStat(String name, String position) {

        Hashtable<String, String> dict = new Hashtable<String, String>();
        // NAME:[G/A/S/TB]
        Hashtable<String, ArrayList> playersStats = new Hashtable<String, ArrayList>();
        // FORMA->  RYIAD MAHREZ:[G:13,A:12,SH:25,TB:10,SOT:19,BCM:6,INT:10,BLK:2,TCK:5,ELG:0,CS:0,GC:0,SAV:0,PS:0]
        Player first = new Player(name);
        System.out.println(first.name);

        // Stats LIST
        List<String> stats = List.of(first.goals_url, first.assists_url, first.shots_url, first.through_ball_url,first.shots_on_target, first.big_chances_missed,
                                    first.interceptions, first.blocks, first.tackles, first.errors_leading_to_goal, first.clean_sheets, first.goals_conceded,
                                    first.saves, first.penalty_saves);

        for (int i = 0; i < stats.size(); i++) {
                String URL = stats.get(i);
                // open browser
                System.setProperty("webdriver.chrome.driver", "C:\\PROJECTS\\chromedriver.exe");
                WebDriver driver = new ChromeDriver();

                try {
                    driver.get(URL);
                    WebElement xx_button = driver.findElement(By.id("onetrust-close-btn-container"));
                    xx_button.click();
                    WebElement x_button = driver.findElement(By.id("advertClose"));
                    // x_button.click();
                    Thread.sleep(2000);

                    boolean pages_available = true;

                    while (pages_available) {
                        if (xx_button.isDisplayed()) {
                            xx_button.click();
                        }

                        List<WebElement> rows = driver.findElements(By.cssSelector(".stats-table tbody tr"));
                        // fetch data
                        for (WebElement row : rows) {
                            //  String rank = row.findElement(By.className("stats-table__rank")).getText();
                            //  String teamName = row.findElement(By.className("stats-table__cell-icon-align")).getText();
                            //  String rank = row.findElement(By.className("stats-table__rank")).getText();
                            String playerName = row.findElement(By.cssSelector(".playerName")).getText();
                            int STAT = Integer.parseInt(row.findElement(By.className("stats-table__main-stat")).getText());

                            if (!playersStats.containsKey(playerName)) {
                                ArrayList<Integer> statsList = new ArrayList<>();
                                for (int j = 0; j < stats.size(); j ++){
                                    statsList.add(0);
                                }
                                playersStats.put(playerName, statsList);
                            }

                            ArrayList<Integer> staturi = playersStats.get(playerName);
                            staturi.set(i, STAT);
                            playersStats.put(playerName, staturi);
                            System.out.println(playerName + " Stats: " + staturi);
                        }

                        try { // next page button check
                            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
                            WebElement nextButton = wait.until(ExpectedConditions.elementToBeClickable(By.className("paginationNextContainer")));

                            if (!nextButton.isDisplayed() || !nextButton.isEnabled()) {
                                pages_available = false;
                            } else {
                                nextButton.click();
                                wait.until(ExpectedConditions.stalenessOf(rows.get(0)));

                            }
                        } catch (Exception e) {
                            pages_available = false;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    driver.quit();
            }
        }
        if (playersStats.containsKey(name)) {
            System.out.println(" ");
            System.out.println(name.toUpperCase() + " STATS: [G/A/SH/TB/SOT/BCM/INT/BLK/TCK/ELG/CS/GC/SAV/PS] " + playersStats.get(name));
        } else {
            System.out.println("Player stats not found.");
        }

        return "DONE";

    }
    public static void main(String[] args) {
        System.out.println(GetStat("Mohamed Salah", "attacker"));
    }
}