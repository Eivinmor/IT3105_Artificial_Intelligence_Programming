import gym
import random

LEFT = 0
DOWN = 1
RIGHT = 2
UP = 3
arrows = ['L', 'D', 'R', 'U']


max_episodes = 10000
epsilon = 0.1
epsilon_decay = 0.999999
discount = 0.99
learning_rate = 0.1


def run_algorithm(env, q_dict):
    observation = env.reset()
    c = 1
    done = False
    global epsilon
    first_time = True
    # print("Timestep:", c)
    # print("Initial board state:")
    # env.render()
    # print("Running algorithm:")

    while not done:
        c += 1
        action = get_eps_greedy_action(q_dict[observation])
        prev_observation = observation
        observation, reward, done, info = env.step(action)
        set_q_value(prev_observation, observation, action, reward, q_dict)
        epsilon *= epsilon_decay
    return reward

    # print("Timestep:", c)
        # env.render()
        # print()
    # env.render()
    # print("Episode finished after", c, "timesteps")
    # print("Done",done)
    # print("Reward:", reward)


def get_eps_greedy_action(q_values):
    random_nr = random.random()
    # print("1-epsilon:", 1-eps)
    # print("Random nr:", random_nr)
    if 1-epsilon > random_nr and max(q_values) != 0:
        best_action = q_values.index(max(q_values))
        # print("Performing best action:", arrows[best_action])
        return best_action
    random_action = random.randint(0, 3)
    # print("Performing random action:", arrows[random_action])
    return random_action


def print_info(info):
    for key, v in info.items():
        print("Key:", key, "Value:", v)


def set_q_value(prev_observation, observation, direction, reward, q_dict):
    q_dict[prev_observation][direction] += learning_rate*(reward + discount*(max(q_dict[observation])) - q_dict[prev_observation][direction])
    # Q(s_t, a_t) += a[r_t+1 + Y * (max(a) Q(s_t+1, a)) - Q(s_t, a_t)]


def print_q_values(q_dict):
    for i in range(16):
        if i % 4 == 0:

            for j in range(4):
                string = "    "+str('{0:.2f}'.format(q_dict[i][3]))
                while len(string) < 12:
                    string += " "
                print(string, end="   ")
            print()

        if i % 4 == 1:
            for j in range(4):
                string = str('{0:.2f}'.format(q_dict[i][0]))+"    "+str('{0:.2f}'.format(q_dict[i][2]))
                while len(string) < 12:
                    string += " "
                print(string, end="   ")
            print()

        if i % 4 == 2:
            for j in range(4):
                string = "    "+str('{0:.2f}'.format(q_dict[i][1]))
                while len(string) < 12:
                    string += " "
                print(string, end="   ")
            print()
        if i % 4 == 3:
            print()


def print_q_values(q_dict):
    for i in range(4):
        for j in range(4):
            q_val = q_dict[i*4+j][3]
            print("{:>16}".format("{0:.2f}".format(q_val)), end="\t\t")
        print()
        for j in range(4):
            q_val1 = q_dict[i*4+j][0]
            q_val2 = q_dict[i*4+j][2]
            print("{:>10}".format("{0:.2f}".format(q_val1)), "{:>11}".format("{0:.2f}".format(q_val2)), end="\t")
        print()
        for j in range(4):
            q_val = q_dict[i*4+j][1]
            print("{:>16}".format("{0:.2f}".format(q_val)), end="\t\t")
        print("\n")
    print("\n\n\n")


def initiate_q_dict(n):
    q_dict = {}
    for i in range(n):
        q_dict[i] = [0]*4
    return q_dict


def main():
    total_rewards = 0
    q_dict = initiate_q_dict(16)
    env = gym.make('FrozenLake-v0')
    episode = 0
    while episode < max_episodes:
        total_rewards += run_algorithm(env, q_dict)
        episode += 1
    print_q_values(q_dict)
    print(total_rewards/episode)
    env.render()

main()
